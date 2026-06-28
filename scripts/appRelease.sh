#!/usr/bin/env bash
set -euo pipefail

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
NC='\033[0m'

# Validate version argument
if [ $# -ne 1 ]; then
    echo -e "${RED}Usage: $0 <version>${NC}"
    echo -e "${RED}Example: $0 v1.0.0${NC}"
    exit 1
fi

VERSION="$1"

# Validate version format
if [[ ! "$VERSION" =~ ^v[0-9]+\.[0-9]+\.[0-9]+$ ]]; then
    echo -e "${RED}ERROR: Version must be in format vX.Y.Z (e.g. v1.0.0)${NC}"
    echo -e "${RED}Got: $VERSION${NC}"
    exit 1
fi

echo -e "${CYAN}========================================${NC}"
echo -e "${CYAN}FightingNerd App Release — ${VERSION}${NC}"
echo -e "${CYAN}========================================${NC}"
echo ""
echo -e "${YELLOW}This will:${NC}"
echo -e "${YELLOW}  1. Snapshot dev into app_release branch${NC}"
echo -e "${YELLOW}  2. Push app_release to origin${NC}"
echo -e "${YELLOW}  3. Trigger deploy-app.yml workflow${NC}"
echo -e "${YELLOW}     - Tags app-${VERSION}${NC}"
echo -e "${YELLOW}     - Builds signed AAB + APK${NC}"
echo -e "${YELLOW}     - Uploads AAB to Play Store internal track (draft)${NC}"
echo -e "${YELLOW}     - Creates GitHub Release App ${VERSION}${NC}"
echo ""
echo -e "${YELLOW}Reminder: gradle.properties should have app.version.name=${VERSION#v} and bumped app.version.code${NC}"
echo ""
read -p "Type 'RELEASE' to confirm: " CONFIRM
if [ "$CONFIRM" != "RELEASE" ]; then
    echo -e "${RED}Cancelled.${NC}"
    exit 1
fi

echo ""
echo -e "${CYAN}Updating dev...${NC}"
git checkout dev
git pull origin dev

echo ""
echo -e "${CYAN}Snapshotting dev into app_release...${NC}"
git checkout app_release
git rm -rf . --ignore-unmatch > /dev/null
git checkout dev -- .
git add .
git commit -m "${VERSION}"

echo ""
echo -e "${CYAN}Returning to dev...${NC}"
git checkout dev

echo ""
echo -e "${CYAN}Pushing app_release...${NC}"
git push origin app_release

echo ""
echo -e "${GREEN}========================================${NC}"
echo -e "${GREEN}Released ${VERSION}${NC}"
echo -e "${GREEN}========================================${NC}"
echo -e "${GREEN}GitHub Actions will now build and upload to Play Store.${NC}"
echo -e "${GREEN}Watch progress: https://github.com/Sophon/FightingNerd/actions${NC}"