#!/bin/sh
set -e

# 환경변수로 ES_URL, INDEX_NAME 받되, 없으면 기본값 사용
ES_URL="${ES_URL:-http://elasticsearch:9200}"
INDEX_NAME="${INDEX_NAME:-bars}"

INDEX_JSON_PATH="${INDEX_JSON_PATH:-/init/index_bars.json}"
SEED_NDJSON_PATH="${SEED_NDJSON_PATH:-/init/bars_seed.ndjson}"

echo "Elasticsearch 초기화 스크립트 시작"
echo "ES_URL = $ES_URL"
echo "INDEX_NAME = $INDEX_NAME"
echo "INDEX_JSON_PATH = $INDEX_JSON_PATH"
echo "SEED_NDJSON_PATH = $SEED_NDJSON_PATH"

echo "Elasticsearch 올라올 때까지 대기 중..."

# ES가 뜰 때까지 반복
until curl -s "$ES_URL" >/dev/null; do
  echo "  아직 응답 없음. 5초 후 재시도..."
  sleep 5
done

echo "Elasticsearch 응답 확인 완료!"

echo "1) 인덱스 생성 및 매핑 적용..."

curl -s -X PUT "$ES_URL/$INDEX_NAME" \
  -H "Content-Type: application/json" \
  --data-binary @"$INDEX_JSON_PATH"

echo
echo "인덱스 생성 요청 완료"

echo "2) Bulk 데이터 업로드..."

curl -s -X POST "$ES_URL/_bulk?refresh=true" \
  -H "Content-Type: application/x-ndjson" \
  --data-binary @"$SEED_NDJSON_PATH"

echo
echo "Bulk 업로드 완료"

echo "Elasticsearch 초기 데이터 세팅 완료 ✅"
