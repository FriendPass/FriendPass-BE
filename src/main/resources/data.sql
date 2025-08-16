
INSERT INTO schools (school_name, domain) VALUES
                                              ('성신여자대학교','sungshin.ac.kr'),
                                              ('국민대학교','kookmin.ac.kr'),
                                              ('동덕여자대학교','dongduk.ac.kr'),
                                              ('서울대학교','snu.ac.kr'),
                                              ('연세대학교','yonsei.ac.kr'),
                                              ('고려대학교','korea.ac.kr'),
                                              ('한양대학교','hanyang.ac.kr'),
                                              ('이화여자대학교','ewha.ac.kr'),
                                              ('중앙대학교','cau.ac.kr'),
                                              ('서강대학교','sogang.ac.kr'),
                                              ('건국대학교','konkuk.ac.kr'),
                                              ('홍익대학교','hongik.ac.kr')
    ON DUPLICATE KEY UPDATE school_name = VALUES(school_name);


INSERT IGNORE INTO interest_tags (interest_name) VALUES
    ('k-pop'),
    ('맛집'),
    ('사진'),
    ('산책'),
    ('쇼핑'),
    ('역사'),
    ('예술'),
    ('전통문화'),
    ('책/독서'),
    ('축제'),
    ('카페'),
    ('패션'),
    ('한국문화체험'),
    ('힐링');
