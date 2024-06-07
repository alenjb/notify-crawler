package com.example.notify_crawler.common.domain;

public enum NoticeCategory {
        ACAD("학사"),         // 학사
        ADM("입학"),          // 입학
        GRAD("졸업"),         // 졸업
        SUG("수강신청"),      // 수강신청
        ABEEK("ABEEK"),       // ABEEK
        INT("현장실습"),      // 현장실습
        EMP("취업"),          // 취업
        SCH("장학"),          // 장학
        REC("채용/모집"),     // 채용/모집
        EVT_SEM("행사/세미나"), // 행사/세미나
        CON_SEM("공모전/세미나"), // 공모전/세미나
        GEN("일반"),          // 일반
        SC("학생회"),         // 학생회
        ETC("기타");          // 아무데도 속하지 않는 경우

        private final String koreanWord;

        NoticeCategory(String koreanWord) {
            this.koreanWord = koreanWord;
        }

        public String getKoreanWord() {
            return koreanWord;
        }

    /**
     * 한국어를 카테고리 타입으로 바꾸는 메서드
     * @param koreanWord 한국어 형식의 카테고리 타입
     * @return 카테고리 타입
     */
    public static NoticeCategory getCategoryFromKoreanWord(String koreanWord) {
        if (koreanWord == null) {
            return NoticeCategory.ETC;
        }
        for (NoticeCategory category : NoticeCategory.values()) {
            if (category.getKoreanWord().equals(koreanWord)) {
                return category;
            }
        }
        return NoticeCategory.ETC;
    }
}