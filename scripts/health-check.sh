#!/bin/bash

# 健康检查脚本
# 用于监控服务状态

set -e

FRONTEND_URL=${FRONTEND_URL:-http://localhost}
BACKEND_URL=${BACKEND_URL:-http://localhost:8080}

check_service() {
    local name=$1
    local url=$2
    
    if curl -f -s "$url" > /dev/null 2>&1; then
        echo "✓ $name 健康"
        return 0
    else
        echo "✗ $name 不健康"
        return 1
    fi
}

echo "执行健康检查..."
echo ""

check_service "前端服务" "$FRONTEND_URL/health"
check_service "后端服务" "$BACKEND_URL/actuator/health"

echo ""
echo "健康检查完成"

