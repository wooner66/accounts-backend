# accounts-backend — 테스트 가이드

이 문서는 **로컬에서 이 저장소를 클론 → 실행 → 테스트** 해볼 수 있도록 단계별로 정리한 가이드입니다.

---

## 0. 개요 / 목적
- 프로젝트: Kotlin + Spring Boot 기반 `accounts-backend`
- 목적: 로컬에서 애플리케이션을 띄우고(선택) 단위/통합 테스트를 실행해 결과를 검증할 수 있도록 안내
- 주요 검증 포인트: 빌드 성공, 서버 실행, 단위(Unit) 테스트 통과, 통합(Integration) 테스트(옵션) 통과

---

## 1. 전제 조건
- JDK 21 이상 설치
- Git
- Docker & Docker Compose (통합 테스트나 Kafka/Redis 확인 시 필요)
- (권장) IDE: IntelliJ IDEA
- 프로젝트 루트를 기준으로 명령을 실행합니다.

---

## 2. 저장소 클론
```bash
git clone https://github.com/wooner66/accounts-backend.git
cd accounts-backend
```

---

## 3. 로컬 인프라 시작 — Docker Compose
레포지토리에 `docker-compose.yml`이 이미 포함되어 있으므로, 통합 테스트나 Kafka/Redis 연동을 확인하려면 아래를 실행하세요.

```bash
docker compose up -d
```

중지/정리:
```bash
docker compose down
```

> 참고: Kafka(또는 Confluent 이미지)는 초기화/기동이 느립니다. 단위 테스트만 확인할 경우 Docker는 필요하지 않습니다.

---

## 4. 빌드
Gradle Wrapper 사용을 권장합니다.

```bash
./gradlew clean build
```

빌드 성공시 jar가 `build/libs/`에 생성됩니다.

---

## 5. 애플리케이션 로컬 실행 (선택)
로컬에서 서버를 구동해 API 동작을 보려면:

```bash
# local profile로 실행
./gradlew bootRun --args='--spring.profiles.active=local'
```

웹서버 기본 포트: `8080`

---

## 1) 회원가입 (Create User)
- URL: `POST /users`
- 설명: 계정/암호/성명/주민등록번호 앞 6자리(yyMMdd)/핸드폰/주소(대분류/상세) 입력

요청 예:
```bash
curl -i -X POST "http://localhost:8080/users"   -H "Content-Type: application/json"   -d '{
    "username": "alice01",
    "password": "P@ssw0rd!",
    "name": "Alice Kim",
    "residentRegistrationNumber": "880818-1234567",
    "phoneNumber": "010-1234-5678",
    "address": "서울특별시",
    "addressDetail": "강남구 역삼동 1-2"
  }'
```

## 2) 관리자: 회원 조회 (페이징 + 검색)
- URL: `GET /admin/users`
- 인증: Basic Auth (`admin:1212`)
- 설명: 페이지네이션과 검색 파라미터를 지원합니다. (예: `page`, `size`, `username`, `name`, `phone`, `ids` 등)

예: 페이지/사이즈로 조회
```bash
curl -i -u admin:1212 "http://localhost:8080/admin/users?page=0&pageSize=50"
```

예: 이름으로 부분검색
```bash
curl -i -u admin:1212 "http://localhost:8080/admin/users?name=Ali"
```

## 3) 관리자: 회원 수정 (암호 / 주소)
- URL: `PATCH /admin/users/{userId}`
- 인증: Basic Auth (`admin:1212`)
- 설명: 암호 또는 주소(또는 둘 다)를 수정할 수 있습니다.

예: 비밀번호만 변경
```bash
curl -i -X PATCH "http://localhost:8080/admin/users/1" \
   -u admin:1212\   -H "Content-Type: application/json" \
   -d '{"password":"NewP@ssw0rd!"}'
```

예: 주소만 변경
```bash
curl -i -X PATCH "http://localhost:8080/admin/users/1" \  
    -u admin:1212 \
    -H "Content-Type: application/json" \
    -d '{"address":"부산광역시", "addressDetail":"해운대구 2-3"}'
```

## 4) 관리자: 회원 삭제
- URL: `DELETE /admin/users/{userId}`
- 인증: Basic Auth (`admin:1212`)

예:
```bash
curl -i -X DELETE "http://localhost:8080/admin/users/1" \
    -u admin:1212
```

## 5) 사용자: 로그인
```bash
curl -i -X POST "http://localhost:8080/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "alice01",
    "password": "P@ssw0rd!"
  }'
```

## 6) 본인 상세정보 조회 (내 정보)
- URL: `POST /users/me`
- 설명: 인증된 사용자가 자신의 상세 정보를 조회합니다. 이 API는 주소의 **최상위 행정구역(예: 서울특별시)**만 반환합니다.

요청 예 (Bearer 토큰 사용):
```bash
curl -i -X POST "http://localhost:8080/users/me" \
    -H "Authorization: Bearer <YOUR_JWT_TOKEN>" \
    -H "Content-Type: application/json"
```

## 7) 관리자: 연령대별 전체 메시지 전송 (비동기)
- URL: `POST /admin/send_message/all`
- 인증: Basic Auth (`admin:1212`)
- 설명: 요청을 받으면 비동기 작업으로 메시지 전송을 스케줄하고 즉시 응답을 돌려줍니다. `age`는 0,10,20,...,90 중 하나로 해석되어 10개 구간 중 해당 연령대에 전송됩니다.

요청 예 (20대에게 전송):
```bash
curl -i -X POST "http://localhost:8080/admin/send_message/all" \
    -u admin:1212 \
    -H "Content-Type: application/json" \
    -d '{"message":"안녕하세요! 20대 대상 이벤트 안내입니다.","age":20}'
```
