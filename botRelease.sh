#!/bin/bash

# Exit on any error
set -e

# Color codes for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Check if version parameter is provided
if [ -z "$1" ]; then
    echo -e "${RED}Error: Version parameter is required${NC}"
    echo "Usage: ./botRelease.sh <version>"
    echo "Example: ./botRelease.sh 3.0.3"
    exit 1
fi

VERSION="$1"

echo -e "${GREEN}Starting bot release process for version: ${VERSION}${NC}"

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

# Confirmation prompt
echo -e "${YELLOW}Ready to push version ${VERSION} to release_bot and trigger deployment.${NC}"
echo -e "${YELLOW}Type 'go' to proceed: ${NC}"
read -r confirmation

if [ "$confirmation" != "go" ]; then
    echo -e "${RED}Deployment cancelled.${NC}"
    exit 1
fi

# Step 5: Push and trigger deployment
echo -e "${YELLOW}Step 5: Pushing to trigger deployment...${NC}"
git push origin release_bot

echo -e "${GREEN}✓ Bot release ${VERSION} completed successfully!${NC}"
