#!/bin/bash

GREEN_BOLD="\033[1;32m"
OFF="\033[m"

SERVER_ADDR="http://localhost:8080"
USER_NAME="Charlie%20Chiang"
USER_ID="2019211915"
DUSTBIN_NAME="BUPT%20S"
DUSTBIN_LATITUDE="40.1564221"
DUSTBIN_LONGITUDE="116.283188"

# Add user
echo -e "${GREEN_BOLD}/add-user: create a user named ${USER_NAME} with an ID of ${USER_ID}.${OFF}"
curl -X POST "${SERVER_ADDR}/add-user" -d "id=${USER_ID}&name=${USER_NAME}"

echo -e "\n"; read -p "Press any key to continue..." -n1 -s; echo -e "\n"

# Get user
echo -e "${GREEN_BOLD}/get-user: retrieve the user's name.${OFF}"
curl -s -X GET "${SERVER_ADDR}/get-user?id=${USER_ID}"

echo -e "\n"; read -p "Press any key to continue..." -n1 -s; echo -e "\n"

# Add dustbin
echo -e "${GREEN_BOLD}/add-dustbin: create a dustbin named ${DUSTBIN_NAME} with a coordinate of (${DUSTBIN_LONGITUDE}, ${DUSTBIN_LATITUDE}).${OFF}"
DUSTBIN_ID=("$(curl -X POST "${SERVER_ADDR}/add-dustbin" -d "name=${DUSTBIN_NAME}&latitude=${DUSTBIN_LATITUDE}&longitude=${DUSTBIN_LONGITUDE}")")
echo -e "Created dustbin with ID=${DUSTBIN_ID}"

echo -e "\n"; read -p "Press any key to continue..." -n1 -s; echo -e "\n"

# Add a couple of wastes
echo -e "${GREEN_BOLD}/add-waste: add wastes to user ${USER_NAME} in dustbin ${DUSTBIN_ID}.${OFF}"
curl -X POST "${SERVER_ADDR}/add-waste" -d "id=${USER_ID}&category=HAZARDOUS_WASTE&weight=0.58&dustbinid=${DUSTBIN_ID}&time=2020-12-12%2010:10:10"
curl -X POST "${SERVER_ADDR}/add-waste" -d "id=${USER_ID}&category=RECYCLABLE_WASTE&weight=0.34&dustbinid=${DUSTBIN_ID}&time=2020-12-12%2019:10:10"
curl -X POST "${SERVER_ADDR}/add-waste" -d "id=${USER_ID}&category=FOOD_WASTE&weight=0.67&dustbinid=${DUSTBIN_ID}&time=2020-12-13%2010:18:10"
curl -X POST "${SERVER_ADDR}/add-waste" -d "id=${USER_ID}&category=RESIDUAL_WASTE&weight=0.98&dustbinid=${DUSTBIN_ID}&time=2020-12-14%2014:10:10"

echo -e "\n"; read -p "Press any key to continue..." -n1 -s; echo -e "\n"

# Retrieve the list of thrown wastes
echo -e "${GREEN_BOLD}/get-waste-list-all: retrieve the list of thrown wastes of user ${USER_NAME}.${OFF}"
curl -s -X GET "${SERVER_ADDR}/get-waste-list-all?id=${USER_ID}" | python -m json.tool

echo -e "\n"; read -p "Press any key to continue..." -n1 -s; echo -e "\n"

# Report an incorrect categorization
echo -e "${GREEN_BOLD}/report-incorrect-categorization: report an incorrect categorization.${OFF}"
curl -X POST "${SERVER_ADDR}/report-incorrect-categorization" -d "dustbinid=${DUSTBIN_ID}&time=2020-12-13%2010:19:09"

echo -e "\n"; read -p "Press any key to continue..." -n1 -s; echo -e "\n"

# Retrieve the list of thrown wastes again to check if the incorrect categorization was marked.
echo -e "${GREEN_BOLD}/get-waste-list-all: check if the incorrect categorization was marked.${OFF}"
curl -s -X GET "${SERVER_ADDR}/get-waste-list-all?id=${USER_ID}" | python -m json.tool

echo -e "\n"; read -p "Press any key to continue..." -n1 -s; echo -e "\n"
