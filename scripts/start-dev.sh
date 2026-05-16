#!/bin/bash

# --- Colors for Output ---
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# 1. Get the current dynamic IP of WSL
WSL_IP=$(hostname -I | awk '{print $1}')
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
LOG_DIR="$PROJECT_ROOT/logs"

# Create logs directory if it doesn't exist
mkdir -p "$LOG_DIR"

echo -e "${BLUE}==================================================================${NC}"
echo -e "${BLUE} 🚀 Bootstrapping Invest Track Environment...${NC}"
echo -e "${BLUE} 🌐 Current WSL IP: $WSL_IP${NC}"
echo -e "${BLUE}==================================================================${NC}"

# 2. Automate Windows Port Proxy (PowerShell)
echo -e "${YELLOW}🌐 Configuring Windows Port Proxy (netsh)...${NC}"
powershell.exe -Command "Start-Process powershell -ArgumentList '-Command \"netsh interface portproxy delete v4tov4 listenport=80 listenaddress=0.0.0.0; netsh interface portproxy add v4tov4 listenport=80 listenaddress=0.0.0.0 connectport=80 connectaddress=$WSL_IP\"' -Verb RunAs"

# 3. Configure Nginx
echo -e "${YELLOW}⚙️  Configuring Nginx...${NC}"
NGINX_CONF_SRC="$PROJECT_ROOT/nginx/invest-track.conf"
NGINX_CONF_DEST="/etc/nginx/sites-enabled/invest-track.conf"

if [ -f "$NGINX_CONF_SRC" ]; then
    sudo cp "$NGINX_CONF_SRC" "$NGINX_CONF_DEST"
    if sudo nginx -t &>/dev/null; then
        sudo systemctl reload nginx
        echo -e "${GREEN}✅ Nginx reloaded successfully.${NC}"
    else
        echo -e "${RED}❌ Nginx configuration test failed!${NC}"
        sudo nginx -t
        exit 1
    fi
else
    echo -e "${RED}❌ Error: Nginx config file not found at $NGINX_CONF_SRC${NC}"
fi

# 4. Start the Backend (Spring Boot) in the background
echo -e "${YELLOW}🟢 Starting Backend (Spring Boot)...${NC}"
echo -e "${BLUE}   📝 Logs: logs/backend.log${NC}"
cd "$PROJECT_ROOT/api"
./gradlew bootRun > "$LOG_DIR/backend.log" 2>&1 &
BACKEND_PID=$!

# 5. Start the Frontend (Astro) in the background
echo -e "${YELLOW}🟢 Starting Frontend (Astro)...${NC}"
echo -e "${BLUE}   📝 Logs: logs/frontend.log${NC}"
cd "$PROJECT_ROOT/ui"
npm run dev -- --host 0.0.0.0 > "$LOG_DIR/frontend.log" 2>&1 &
FRONTEND_PID=$!

# 6. Trap Ctrl+C (SIGINT) to kill both processes cleanly when you exit
function cleanup {
    echo -e "\n${RED}🛑 Stopping servers...${NC}"
    [ -n "$BACKEND_PID" ] && kill $BACKEND_PID 2>/dev/null
    [ -n "$FRONTEND_PID" ] && kill $FRONTEND_PID 2>/dev/null
    exit
}
trap cleanup SIGINT SIGTERM

# 7. Health Check (Wait for servers to be ready)
echo -e "${YELLOW}⏳ Waiting for servers to start...${NC}"
MAX_RETRIES=60
RETRY_COUNT=0

while true; do
    # Check if processes are still running
    if ! kill -0 $BACKEND_PID 2>/dev/null; then
        echo -e "\n${RED}❌ Backend failed to start. Check logs: logs/backend.log${NC}"
        cleanup
    fi
    if ! kill -0 $FRONTEND_PID 2>/dev/null; then
        echo -e "\n${RED}❌ Frontend failed to start. Check logs: logs/frontend.log${NC}"
        cleanup
    fi

    # Check health endpoints
    BACKEND_UP=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/invest-track/api/heartbeat)
    FRONTEND_UP=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:4321/invest-track/)

    if [ "$BACKEND_UP" == "200" ] && [ "$FRONTEND_UP" == "200" ]; then
        echo -e "\n${GREEN}✅ Servers are UP!${NC}"
        break
    fi

    if [ $RETRY_COUNT -ge $MAX_RETRIES ]; then
        echo -e "\n${RED}❌ Servers took too long to start.${NC}"
        echo -e "${YELLOW}Check backend logs: tail -n 20 logs/backend.log${NC}"
        echo -e "${YELLOW}Check frontend logs: tail -n 20 logs/frontend.log${NC}"
        cleanup
    fi

    echo -n "."
    sleep 2
    RETRY_COUNT=$((RETRY_COUNT+1))
done

echo -e "${BLUE}==================================================================${NC}"
echo -e "${GREEN} 🌍 Application: http://apps.home/invest-track/${NC}"
echo -e "${GREEN} 💓 Heartbeat:   http://apps.home/invest-track/api/heartbeat${NC}"
echo -e "${BLUE}==================================================================${NC}"
echo -e "${YELLOW}💡 Tip: Use 'tail -f logs/backend.log' to see live backend output.${NC}"

# 8. Wait for the background processes to keep the script running
wait $BACKEND_PID
wait $FRONTEND_PID
