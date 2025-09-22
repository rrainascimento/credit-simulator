import org.mockito.Mockito

/**
 * Helper para facilitar uso do Mockito com Kotlin (evita problemas de nullability).
 */
object MockitoHelper {
    private inline fun <reified T> any(): T {
        Mockito.any<T>()
        return uninitialized()
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T> uninitialized(): T = null as T

    fun <T> eq(value: T): T = Mockito.eq(value)
}
