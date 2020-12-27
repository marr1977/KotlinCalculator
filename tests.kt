import org.junit.Assert
import org.junit.Test

class Tests {

    @Test
    fun numericOnly() {
        Assert.assertEquals(2.5, Parser().parse("2.5").getResult())
    }

    @Test
    fun plus() {
        Assert.assertEquals(21.0, Parser().parse("17 +4").getResult())
    }

    @Test
    fun minus() {
        Assert.assertEquals(11.0, Parser().parse("13-2").getResult())
    }

    @Test
    fun mul() {
        Assert.assertEquals(128.0, Parser().parse("64* 2").getResult())
    }

    @Test
    fun div() {
        Assert.assertEquals(4.0, Parser().parse("24 / 6").getResult())
    }

    @Test
    fun exp() {
        Assert.assertEquals(16.0, Parser().parse("2^4").getResult())
    }

    @Test
    fun grouping() {
        Assert.assertEquals(8.0, Parser().parse("2^(1+3)-(2*(2+(4/2)))").getResult())
    }

}