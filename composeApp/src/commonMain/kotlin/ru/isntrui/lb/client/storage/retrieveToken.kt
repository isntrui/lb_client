import ru.isntrui.lb.client.storage.TokenStorage

fun retrieveToken(): String? {
    return TokenStorage.getToken()
}