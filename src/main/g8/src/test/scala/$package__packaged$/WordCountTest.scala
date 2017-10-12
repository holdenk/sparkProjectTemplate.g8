package $organization$.$name$

/**
 * A simple test for everyone's favourite wordcount example.
 */

import com.holdenkarau.spark.testing.SharedSparkContext
import org.apache.spark.sql.SQLContext
import org.scalatest.FunSuite

class WordCountTest extends FunSuite with SharedSparkContext {
  test("countWords should  produce expected behaviours"){
    val spark =new SQLContext(sc).sparkSession
    import spark.implicits._

    val linesDs = Seq(
      "How happy was the panda? You ask.",
      "Panda is the most happy panda in all the#!?ing land!").toDS
    val stopWords: Set[String] = Set("a", "the", "in", "was", "there", "she", "he")
    val splitTokens: Array[Char] = "#%?!. ".toCharArray
    val actualResultDs = WordCount.withStopWordsFiltered(linesDs, splitTokens, stopWords)
    val actualResultMap = actualResultDs.collectAsMap()
    assert(!actualResultMap.contains("the"))
    assert(!actualResultMap.contains("?"))
    assert(!actualResultMap.contains("#!?ing"))
    assert(actualResultMap.contains("ing"))
    assert(actualResultMap.get("panda").get.equals(3))
  }
}