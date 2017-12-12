package $organization$.$name$

/**
 * Everyone's favourite wordcount example.
 */

import org.apache.spark.sql.Dataset
import com.amazonaws.regions.RegionUtils
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain
import com.amazonaws.services.kinesis.AmazonKinesisClient
import com.amazonaws.services.kinesis.clientlibrary.lib.worker.InitialPositionInStream
import com.typesafe.config.ConfigBeanFactory
import org.apache.spark.SparkConf
import org.apache.spark.storage.StorageLevel
import org.apache.spark.sql.SparkSession
import com.typesafe.config.ConfigFactory
import org.apache.spark.streaming.dstream.ConstantInputDStream
import org.apache.spark.streaming.kinesis.KinesisUtils
import org.apache.spark.streaming.{Milliseconds, Minutes, Seconds, StreamingContext}

object KinesisExample extends App{
  def main(args: Array[String]){
    val conf = new SparkConf().setAppName("Kinesis Read Data")
    conf.setIfMissing("spark.master", "local[*]")
    val kinesisConf = ConfigFactory.load.getConfig("kinesis")
    val appName = kinesisConf.getString("appName")
    val streamName = kinesisConf.getString("streamName")
    val endpointUrl = kinesisConf.getString("endpointUrl")
    val credentials = new DefaultAWSCredentialsProviderChain().getCredentials()
    require(credentials != null, "No AWS credentials found. See http://docs.aws.amazon.com/AWSSdkDocsJava/latest/DeveloperGuide/credentials.html")
    val kinesisClient = new AmazonKinesisClient(credentials)
    kinesisClient.setEndpoint(endpointUrl)
    val numShards  = kinesisClient.describeStream(streamName).getStreamDescription.getShards.size
    val numStreams = numShards
    val batchInterval = Milliseconds(2000)
    val kinesisCheckpointInterval = batchInterval
    val regionName  = RegionUtils.getRegionByEndpoint(endpointUrl).getName
    val ssc = new StreamingContext(conf, batchInterval)

    val kinesisStreams = (0 until numStreams) . map {
      i => KinesisUtils.createStream(
        ssc,
        appName,
        streamName,
        endpointUrl,
        regionName,
        InitialPositionInStream.LATEST,
        kinesisCheckpointInterval,
        StorageLevel.MEMORY_AND_DISK_2
      )
    }
    val unionStreams = ssc.union(kinesisStreams)
    val sensorData = unionStreams.map {
      byteArray =>
      val Array(sensorId, temp, status) = new String(byteArray).split(",")
      SensorData(sensorId, temp.toInt, status)
      val hotSensors : DStream[SensorData] = sensorData.filter(_.currentTemp > 100)
      println(s"Sensor with Temp > 100")
      hotSensors.foreach{ sd =>
        println(s"Sensor id ${sd.id} has temp of ${sd.currentTemp}")
      }
      hotSensors.window(Seconds(20)).foreachRDD { rdd =>
        val spark = SparkSession
          .builder
          .config(rdd.sparkContext.getConf)
          .getOrCreate()
        import spark.implicits._
        val hotSensorDF = rdd.toDF()
        hotSensorDF.createOrReplaceTempView("hot_sensors")
        hottestOverTime = spark.sql("select * from hot_sensors order by currentTemp desc limit 5")
        hottestOverTime.show(2)
      }
      ssc.remember(Minutes(1))
      ssc.start()
      ssc.awaitTemination()
    }
  }
}

case class SensorData(id:String, currentTemp:Int, status:String)
