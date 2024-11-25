package mp.verif_ai.domain.model.auth

data object ExpertFields {
    // 교육/학문
    data object Education {
        const val ELEMENTARY = "초등교육"
        const val SECONDARY = "중/고등교육"
        const val COLLEGE = "대학교육"
        const val LANGUAGE = "어학/외국어"
        const val STUDY_ABROAD = "유학/연수"
    }

    // 컴퓨터/인터넷
    data object IT {
        const val PROGRAMMING = "프로그래밍"
        const val HARDWARE = "컴퓨터/하드웨어"
        const val MOBILE = "모바일/앱"
        const val SECURITY = "보안/해킹"
        const val DIGITAL = "디지털기기"
    }

    // 사회/정치/경제
    data object Society {
        const val POLITICS = "정치/행정"
        const val ECONOMICS = "경제/금융"
        const val LAW = "법률/상담"
        const val LABOR = "노동/직장"
        const val SOCIAL = "사회문제"
    }

    // 생활/건강
    data object Life {
        const val HEALTH = "건강/의학"
        const val FOOD = "음식/요리"
        const val FASHION = "패션/뷰티"
        const val HOUSING = "주택/인테리어"
        const val PET = "반려동물"
    }

    // 문화/예술
    data object Culture {
        const val MUSIC = "음악"
        const val MOVIE = "영화"
        const val ARTS = "미술/디자인"
        const val PERFORMANCE = "공연/전시"
        const val ENTERTAINMENT = "연예/방송"
    }

    // 스포츠/레저
    data object Sports {
        const val EXERCISE = "운동/스포츠"
        const val LEISURE = "레저/여가"
        const val TRAVEL = "여행"
        const val EXTREME = "익스트림스포츠"
        const val ESPORTS = "e스포츠"
    }

    // 취미/생활
    data object Hobby {
        const val GAME = "게임"
        const val CRAFTS = "공예/취미"
        const val COLLECTION = "수집/기록"
        const val PHOTO = "사진/영상"
        const val BOOKS = "도서"
    }

    // 과학/기술
    data object Science {
        const val PHYSICS = "물리/화학"
        const val BIO = "생물/생명"
        const val EARTH = "지구/환경"
        const val ENGINEERING = "공학"
        const val MATH = "수학"
    }

    fun getAllFields(): List<String> {
        return listOf(
            Education.ELEMENTARY, Education.SECONDARY, Education.COLLEGE,
            Education.LANGUAGE, Education.STUDY_ABROAD,

            IT.PROGRAMMING, IT.HARDWARE, IT.MOBILE,
            IT.SECURITY, IT.DIGITAL,

            Society.POLITICS, Society.ECONOMICS, Society.LAW,
            Society.LABOR, Society.SOCIAL,

            Life.HEALTH, Life.FOOD, Life.FASHION,
            Life.HOUSING, Life.PET,

            Culture.MUSIC, Culture.MOVIE, Culture.ARTS,
            Culture.PERFORMANCE, Culture.ENTERTAINMENT,

            Sports.EXERCISE, Sports.LEISURE, Sports.TRAVEL,
            Sports.EXTREME, Sports.ESPORTS,

            Hobby.GAME, Hobby.CRAFTS, Hobby.COLLECTION,
            Hobby.PHOTO, Hobby.BOOKS,

            Science.PHYSICS, Science.BIO, Science.EARTH,
            Science.ENGINEERING, Science.MATH
        )
    }

    fun getFieldsByCategory(): Map<String, List<String>> {
        return mapOf(
            "교육/학문" to listOf(
                Education.ELEMENTARY, Education.SECONDARY, Education.COLLEGE,
                Education.LANGUAGE, Education.STUDY_ABROAD
            ),
            "컴퓨터/인터넷" to listOf(
                IT.PROGRAMMING, IT.HARDWARE, IT.MOBILE,
                IT.SECURITY, IT.DIGITAL
            ),
            "사회/정치/경제" to listOf(
                Society.POLITICS, Society.ECONOMICS, Society.LAW,
                Society.LABOR, Society.SOCIAL
            ),
            "생활/건강" to listOf(
                Life.HEALTH, Life.FOOD, Life.FASHION,
                Life.HOUSING, Life.PET
            ),
            "문화/예술" to listOf(
                Culture.MUSIC, Culture.MOVIE, Culture.ARTS,
                Culture.PERFORMANCE, Culture.ENTERTAINMENT
            ),
            "스포츠/레저" to listOf(
                Sports.EXERCISE, Sports.LEISURE, Sports.TRAVEL,
                Sports.EXTREME, Sports.ESPORTS
            ),
            "취미/생활" to listOf(
                Hobby.GAME, Hobby.CRAFTS, Hobby.COLLECTION,
                Hobby.PHOTO, Hobby.BOOKS
            ),
            "과학/기술" to listOf(
                Science.PHYSICS, Science.BIO, Science.EARTH,
                Science.ENGINEERING, Science.MATH
            )
        )
    }
}