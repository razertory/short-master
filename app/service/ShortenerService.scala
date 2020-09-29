package service

import javax.inject.{Inject, Singleton}
import org.joda.time.DateTime
import redis.clients.jedis.Jedis
import util.Base62

@Singleton
class ShortenerService @Inject()() {

  val jedis = new Jedis("127.0.0.1", 6379)

  def now = DateTime.now

  // 查询或生成
  def getOrGenerate(url: String): String = {
    val short = jedis.get(urlKey(url))
    if (short != null) {
      short
    } else {
      generateAndSave(url)
    }
  }

  def generateAndSave(url: String): String = {
    val key = counterKey(now)
    val shortBase = generate(key)
    persist(url, shortBase)
    shortBase
  }

  // 生成唯一短链, 短链算法
  def generate(key: String): String = {
    val num = jedis.incr(key)
    val s = "" + now.monthOfYear().get() + now.dayOfMonth().get() + num
    print(s)
    Base62.encode(s.toLong)
  }

  // 存储 url 和 short
  def persist(url: String, short: String): Unit = {
    jedis.set(urlKey(url), short)
    jedis.set(shortKey(short), url)
  }

  private def counterKey(date: DateTime): String = {
    val y = date.year().get()
    val m = date.monthOfYear().get()
    val d = date.dayOfMonth().get()
    "counter:" + y + ":" + m + ":" + d
  }

  private def urlKey(url: String): String = {
    "url:" + url
  }

  private def shortKey(short: String ): String = {
    "short:" + short
  }

}
