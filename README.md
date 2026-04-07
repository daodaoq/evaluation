# evaluation（后端）

本项目是综合素质评价系统的后端服务，负责用户认证、综测规则管理、学生申报与审核流程、分数计算与公示异议，以及基于 RAG 的综测细则智能问答能力。

## 一、项目定位

- 面向学院综测业务的统一后端 API
- 提供学生端与管理端所需的业务接口
- 提供 AI 问答能力：基于 `综测细则.json` 做向量检索增强生成（RAG）

## 二、核心功能

- 认证与权限
  - JWT 登录鉴权
  - Spring Security 权限控制
- 综测业务
  - 规则/规则项/分类管理
  - 学生申报、材料上传、申诉、异议、公示
  - 分数计算与统计
- AI 细则问答
  - 细则入库：启动时读取 `src/main/resources/综测细则.json`，切块后写入 Chroma
  - 问答接口：支持普通问答与流式输出（SSE）

## 三、技术栈

- Java 21
- Spring Boot 3.4.x
- Spring Security + JWT
- MyBatis
- Spring AI 1.0
- Chroma Vector Store
- Redis
- MinIO
- MySQL 8+

## 四、目录说明（后端内）

- `src/main/java`：业务代码
- `src/main/resources`：配置文件与细则数据
- `src/main/resources/综测细则.json`：RAG 细则数据源
- `docker/docker-compose-chroma.yml`：本地依赖服务（Chroma + Redis + MinIO）
- `sql/`：数据库脚本
- `.env.example`：环境变量示例

## 五、运行环境要求

- JDK 21
- Maven（或使用项目内 `./mvnw`）
- Docker Desktop
- MySQL 8+

## 六、环境配置

### 1) 创建环境文件

复制 `evaluation/.env.example` 为 `evaluation/.env`，并按需填写：

```env
AI_API_KEY=你的百炼Key
AI_ENDPOINT=https://dashscope.aliyuncs.com/compatible-mode/v1/
AI_MODEL=qwen3-max
AI_EMBEDDING_MODEL=text-embedding-v4
SPRING_PROFILES_ACTIVE=default,ai
CHROMA_HOST=http://127.0.0.1
CHROMA_PORT=8000
```

### 2) 关键说明

- `AI_ENDPOINT` 建议保留结尾 `/`，避免路径拼接异常
- 不需要 AI 时，可将 `SPRING_PROFILES_ACTIVE` 去掉 `ai`
- `.env` 已在 `.gitignore` 中，避免提交密钥

## 七、MySQL 初始化（localhost）

后端默认连接本地 MySQL：

- 地址：`localhost:3306`
- 数据库：`evaluation`
- 用户名：`root`
- 密码：`123456`

请先在 MySQL 中创建数据库并导入初始化脚本。

### 1) 创建数据库

```sql
CREATE DATABASE IF NOT EXISTS evaluation
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;
```

### 2) 执行 SQL 文件

在 `evaluation/` 目录执行（你的文件名已指定）：

```bash
mysql -h localhost -P 3306 -u root -p evaluation < sql/_localhost-2026_04_06_11_25_04-dump.sql
```

输入密码后即可导入。

如果你使用的是其他账号/密码，请同步修改 `src/main/resources/application.yml` 中的数据源配置。

## 八、启动依赖服务（推荐）

在 `evaluation/` 目录执行：

```bash
docker compose -f docker/docker-compose-chroma.yml pull
docker compose -f docker/docker-compose-chroma.yml up -d
```

该 compose 会启动：

- Chroma：`http://127.0.0.1:8000`
- Redis：`127.0.0.1:6379`（密码 `123456`）
- MinIO：
  - API：`http://127.0.0.1:9000`
  - Console：`http://127.0.0.1:9001`
  - 账号/密码：`minioadmin / minioadmin`
  - 自动创建 public 桶：`evaluation`

停止服务：

```bash
docker compose -f docker/docker-compose-chroma.yml down
```

删除容器与卷：

```bash
docker compose -f docker/docker-compose-chroma.yml down -v
```

## 九、启动后端

```bash
cd evaluation
./mvnw spring-boot:run
```

默认端口：`8080`

## 十、RAG 问答说明

- 数据源：`src/main/resources/综测细则.json`
- 入库行为：默认启动自动入库（可在 `application.yml` 调整）
- 检索方式：向量检索 + 大模型生成（不是纯规则引擎）

如果你更新了 `综测细则.json`，建议：

1. 清理旧 Chroma collection（避免重复向量）
2. 重启后端触发重新入库

## 十一、常见问题排查

- AI Embedding 报 404
  - 检查 `AI_ENDPOINT` 是否正确，是否带结尾 `/`
  - 检查模型名与百炼兼容路径配置
- AI 接口无响应或一直 loading
  - 查看后端日志是否有 SSE/鉴权异常
  - 确认 token 与权限正常
- 回答命中错误条款
  - 优先清理向量库后重建
  - 检查细则文本结构与关键词是否清晰

## 十二、生产建议

- 使用独立 MySQL / Redis / MinIO / 向量库实例
- 使用环境变量注入密钥，不要硬编码
- 开启日志采集与接口监控
- 对 AI 接口增加限流与超时控制
