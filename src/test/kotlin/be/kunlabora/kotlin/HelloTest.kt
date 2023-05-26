package be.kunlabora.kotlin

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

class HelloTest {

    @Test
    fun `greet - should return World`() {
        Assertions.assertThat(Hello().greet()).isEqualTo("World!")
    }

}