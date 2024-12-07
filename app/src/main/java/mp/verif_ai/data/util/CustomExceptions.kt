package mp.verif_ai.data.util

class CustomExceptions {
    class NetworkException : Exception("네트워크 연결을 확인해주세요")
    class NotFoundException : Exception("요청한 리소스를 찾을 수 없습니다")
    class PermissionDeniedException : Exception("접근 권한이 없습니다")
}