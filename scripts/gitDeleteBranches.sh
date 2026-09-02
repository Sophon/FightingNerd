#!/bin/bash

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${YELLOW}Scanning for branches to delete...${NC}\n"

# Get current branch to avoid deleting it
CURRENT_BRANCH=$(git branch --show-current)

# Get all local branches except:
# - dev
# - branches containing "release"
# - current branch (to avoid issues)
BRANCHES_TO_DELETE=$(git branch --format='%(refname:short)' | \
    grep -v "^dev$" | \
    grep -v "release" | \
    grep -v "^${CURRENT_BRANCH}$")

# Check if there are any branches to delete
if [ -z "$BRANCHES_TO_DELETE" ]; then
    echo -e "${GREEN}No branches found matching the deletion criteria.${NC}"
    exit 0
fi

# Display branches that will be deleted
echo -e "${RED}The following branches will be deleted:${NC}\n"
echo "$BRANCHES_TO_DELETE" | while read -r branch; do
    echo "  - $branch"
done

echo ""
echo -e "${YELLOW}Current branch (${CURRENT_BRANCH}) will be preserved.${NC}"
echo ""

# Ask for confirmation
read -p "Type 'confirm' to delete these branches: " CONFIRMATION

if [ "$CONFIRMATION" = "confirm" ]; then
    echo ""
    echo -e "${YELLOW}Deleting branches...${NC}\n"

    echo "$BRANCHES_TO_DELETE" | while read -r branch; do
        if git branch -D "$branch" 2>/dev/null; then
            echo -e "${GREEN}✓${NC} Deleted: $branch"
        else
            echo -e "${RED}✗${NC} Failed to delete: $branch"
        fi
    done

    echo ""
    echo -e "${GREEN}Done!${NC}"
else
    echo -e "${YELLOW}Deletion cancelled.${NC}"
    exit 0
fi