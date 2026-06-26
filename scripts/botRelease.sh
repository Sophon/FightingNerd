#!/bin/bash

# Exit on any error
set -e

# Color codes for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
BOLD='\033[1m'
NC='\033[0m'

# Check if version parameter is provided
if [ -z "$1" ]; then
    echo -e "${RED}Error: Version parameter with v prefix is required${NC}"
    echo "Usage: ./botRelease.sh v<version>"
    echo "Example: ./botRelease.sh v3.0.3"
    exit 1
fi

VERSION="$1"

# Prompt user to select release mode
echo -e "${BOLD}FightingNerd Bot Release — ${VERSION}${NC}"
echo ""
echo -e "Select release type:"
echo -e "  ${CYAN}IMMEDIATE${NC}  — push now, deploy starts right away"
echo -e "  ${CYAN}SCHEDULED${NC}  — push to staging, GitHub promotes at 00:05 UTC"
echo ""
read -rp "Type IMMEDIATE or SCHEDULED: " MODE_INPUT
echo ""

# Map input to mode name
case "$MODE_INPUT" in
    "IMMEDIATE") MODE="immediate" ;;
    "SCHEDULED") MODE="scheduled" ;;
    *)
        echo -e "${RED}Invalid selection. Aborting.${NC}"
        exit 1
        ;;
esac

# Step 1: Update dev branch
echo -e "${YELLOW}Step 1: Updating dev branch...${NC}"
git checkout dev
git pull origin dev

# Step 2: Switch to release branch
echo -e "${YELLOW}Step 2: Switching to release_bot branch...${NC}"
git checkout release_bot

# Step 3: Replace everything with dev's version
echo -e "${YELLOW}Step 3: Replacing with dev version...${NC}"
git rm -rf .
git checkout dev -- .

# Step 4: Commit the new release
echo -e "${YELLOW}Step 4: Committing changes...${NC}"
git add .
git commit -m "${VERSION}"

git checkout dev

# Step 5a: Push immediately and trigger deployment
if [ "$MODE" = "immediate" ]; then
    echo ""
    echo -e "${YELLOW}This will deploy ${VERSION} immediately.${NC}"
    read -rp "Type 'IMMEDIATE' to confirm: " CONFIRM
    if [ "$CONFIRM" != "IMMEDIATE" ]; then
        echo -e "${RED}Deployment cancelled.${NC}"
        exit 1
    fi
    git push origin release_bot
    echo -e "${GREEN}✓ Bot release ${VERSION} pushed. Deployment starting now.${NC}"

# Step 5b: Push to staging branch — scheduled workflow promotes to release_bot at 00:05 UTC
elif [ "$MODE" = "scheduled" ]; then
    echo ""
    echo -e "${YELLOW}This will stage ${VERSION} for deployment at 00:05 UTC tonight.${NC}"
    read -rp "Type 'SCHEDULED' to confirm: " CONFIRM
    if [ "$CONFIRM" != "SCHEDULED" ]; then
        echo -e "${RED}Deployment cancelled.${NC}"
        exit 1
    fi
    git push origin release_bot:release_bot_pending --force
    echo -e "${GREEN}✓ Bot release ${VERSION} staged. GitHub will deploy at 00:05 UTC.${NC}"
    echo -e "${CYAN}  To cancel: git push origin --delete release_bot_pending${NC}"
fi