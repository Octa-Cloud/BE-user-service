#!/bin/bash

# User Service MySQL 테스트 실행 스크립트

set -e

echo "🚀 User Service MySQL 테스트 시작..."

# 색상 정의
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 함수 정의
print_step() {
    echo -e "${BLUE}📋 $1${NC}"
}

print_success() {
    echo -e "${GREEN}✅ $1${NC}"
}

print_warning() {
    echo -e "${YELLOW}⚠️  $1${NC}"
}

print_error() {
    echo -e "${RED}❌ $1${NC}"
}

# 1. 의존성 서비스 시작
print_step "테스트용 의존성 서비스 시작..."
docker-compose -f docker-compose.test.yml up -d

# 서비스가 준비될 때까지 대기
print_step "서비스 준비 대기 중..."
sleep 15

# MySQL 연결 확인
print_step "MySQL 연결 확인..."
until docker exec user-service-mysql-test mysqladmin ping -h localhost -u testuser -ptestpassword --silent; do
    echo "MySQL 연결 대기 중..."
    sleep 2
done
print_success "MySQL 연결 완료"

# 2. 단위 테스트 실행 (MySQL 프로필 사용)
print_step "단위 테스트 실행 (MySQL)..."
./gradlew test --tests "com.project.user.*" --info -Dspring.profiles.active=test-mysql

if [ $? -eq 0 ]; then
    print_success "단위 테스트 통과"
else
    print_error "단위 테스트 실패"
    exit 1
fi

# 3. 통합 테스트 실행
print_step "통합 테스트 실행..."
./gradlew test --tests "com.project.user.integration.*" --info -Dspring.profiles.active=test-mysql

if [ $? -eq 0 ]; then
    print_success "통합 테스트 통과"
else
    print_error "통합 테스트 실패"
    exit 1
fi

# 4. 컨트롤러 테스트 실행
print_step "컨트롤러 테스트 실행..."
./gradlew test --tests "com.project.user.controller.*" --info -Dspring.profiles.active=test-mysql

if [ $? -eq 0 ]; then
    print_success "컨트롤러 테스트 통과"
else
    print_error "컨트롤러 테스트 실패"
    exit 1
fi

# 5. 전체 테스트 리포트 생성
print_step "테스트 리포트 생성..."
./gradlew jacocoTestReport

# 6. 정리
print_step "테스트용 의존성 서비스 정리..."
docker-compose -f docker-compose.test.yml down

print_success "모든 테스트 완료! 🎉"

# 테스트 결과 요약
echo ""
echo "📊 테스트 결과 요약:"
echo "- 단위 테스트: ✅ 통과"
echo "- 통합 테스트: ✅ 통과" 
echo "- 컨트롤러 테스트: ✅ 통과"
echo "- 데이터베이스: MySQL 8.0"
echo "- 리포트: build/reports/tests/test/index.html"
echo "- 커버리지: build/reports/jacoco/test/html/index.html"
