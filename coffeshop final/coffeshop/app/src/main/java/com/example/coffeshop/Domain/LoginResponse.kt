data class LoginResponse(
    val success: Boolean,
    val access_token: String,
    val expires_in: Int, // Wajib untuk logika kadaluarsa
    val user: UserData?
)

data class UserData(
    val id: Int,
    val name: String,
    val email: String
)