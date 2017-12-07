package $organization$.$name$

/**
 * Everyone's favourite wordcount example.
 */
import org.apache.spark.sql.Dataset

object KinesisExample extends App {
  /**
   * A slightly more complex than normal wordcount example with optional
   * separators and stopWords. Splits on the provided separators, removes
   * the stopwords, and converts everything to lower case.
    */

  def withStopWordsFiltered(ds: Dataset[String],
    separators : Array[Char] = " ".toCharArray,
    stopWords : Set[String] = Set("the")) = {
    val spark = ds.sparkSession
    import spark.implicits._
    val tokens = ds.flatMap(_.split(separators).map(_.trim.toLowerCase))
    val lcStopWords = stopWords.map(_.trim.toLowerCase)
    val words = tokens.filter(token =>
      !lcStopWords.contains(token) && (token.length > 0))
    val wordPairs :Dataset[(String,Int)]= words.map((_, 1))
    val wordCounts = wordPairs.rdd.reduceByKey(_ + _)
    wordCounts
  }
}