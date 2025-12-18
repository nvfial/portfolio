#!/bin/bash

# 企业级部署脚本
# 使用方法: ./scripts/deploy.sh [environment] [version]
# 示例: ./scripts/deploy.sh production v1.0.0

set -e  # 遇到错误立即退出

ENVIRONMENT=${1:-staging}
VERSION=${2:-latest}
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"

# 颜色输出
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 日志函数
log_info() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

log_warn() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# 检查环境
check_environment() {
    log_info "检查部署环境: $ENVIRONMENT"
    
    case $ENVIRONMENT in
        dev|development)
            ENV_FILE=".env.dev"
            ;;
        staging)
            ENV_FILE=".env.staging"
            ;;
        production|prod)
            ENV_FILE=".env.prod"
            if [ "$ENVIRONMENT" == "production" ]; then
                read -p "确认部署到生产环境? (yes/no): " confirm
                if [ "$confirm" != "yes" ]; then
                    log_error "部署已取消"
                    exit 1
                fi
            fi
            ;;
        *)
            log_error "未知环境: $ENVIRONMENT"
            log_info "支持的环境: dev, staging, production"
            exit 1
            ;;
    esac
    
    if [ ! -f "$PROJECT_ROOT/$ENV_FILE" ]; then
        log_warn "环境文件 $ENV_FILE 不存在，使用默认配置"
    fi
}

# 健康检查
health_check() {
    local service=$1
    local url=$2
    local max_attempts=30
    local attempt=1
    
    log_info "检查 $service 健康状态..."
    
    while [ $attempt -le $max_attempts ]; do
        if curl -f -s "$url" > /dev/null 2>&1; then
            log_info "$service 健康检查通过"
            return 0
        fi
        
        log_warn "等待 $service 启动... ($attempt/$max_attempts)"
        sleep 5
        attempt=$((attempt + 1))
    done
    
    log_error "$service 健康检查失败"
    return 1
}

# 部署后端
deploy_backend() {
    log_info "部署后端服务..."
    
    cd "$PROJECT_ROOT/my-portfolio-backend"
    
    # 构建Docker镜像
    log_info "构建后端Docker镜像..."
    docker build -t portfolio-backend:$VERSION -t portfolio-backend:latest .
    
    # 停止旧容器
    if docker ps -a | grep -q portfolio-backend; then
        log_info "停止旧的后端容器..."
        docker stop portfolio-backend || true
        docker rm portfolio-backend || true
    fi
    
    # 启动新容器
    log_info "启动新的后端容器..."
    docker run -d \
        --name portfolio-backend \
        --network portfolio-network \
        -p 8080:8080 \
        --env-file "$PROJECT_ROOT/$ENV_FILE" \
        portfolio-backend:$VERSION
    
    # 健康检查
    sleep 10
    health_check "后端服务" "http://localhost:8080/actuator/health"
}

# 部署前端
deploy_frontend() {
    log_info "部署前端服务..."
    
    cd "$PROJECT_ROOT/my-portfolio"
    
    # 构建Docker镜像
    log_info "构建前端Docker镜像..."
    docker build \
        --build-arg VITE_API_BASE_URL="${API_BASE_URL:-http://localhost:8080}" \
        -t portfolio-frontend:$VERSION \
        -t portfolio-frontend:latest .
    
    # 停止旧容器
    if docker ps -a | grep -q portfolio-frontend; then
        log_info "停止旧的前端容器..."
        docker stop portfolio-frontend || true
        docker rm portfolio-frontend || true
    fi
    
    # 启动新容器
    log_info "启动新的前端容器..."
    docker run -d \
        --name portfolio-frontend \
        --network portfolio-network \
        -p 80:80 \
        portfolio-frontend:$VERSION
    
    # 健康检查
    sleep 5
    health_check "前端服务" "http://localhost/health"
}

# 回滚
rollback() {
    log_warn "开始回滚..."
    
    # 这里添加回滚逻辑
    # 例如：恢复到上一个版本的镜像
    
    log_info "回滚完成"
}

# 主函数
main() {
    log_info "开始部署流程..."
    log_info "环境: $ENVIRONMENT"
    log_info "版本: $VERSION"
    
    # 检查Docker
    if ! command -v docker &> /dev/null; then
        log_error "Docker未安装"
        exit 1
    fi
    
    # 检查环境
    check_environment
    
    # 创建网络（如果不存在）
    if ! docker network ls | grep -q portfolio-network; then
        log_info "创建Docker网络..."
        docker network create portfolio-network
    fi
    
    # 部署
    trap rollback ERR
    
    deploy_backend
    deploy_frontend
    
    log_info "部署完成！"
    log_info "前端: http://localhost"
    log_info "后端: http://localhost:8080"
}

# 执行主函数
main "$@"

