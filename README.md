# waste-sorting-helper-server (dev)

Back end for project "Research on campus garbage sorting and Recycling".

See also: [waste-sorting-helper-client](https://github.com/charlie0129/waste-sorting-helper-client).

*All APIs are for test purposes.*

**This project is still in its alpha stage,  under development. There is no guarantee that the program can run without problems.**

 TODO:

- [x] Basic features including user-addition, waste-addition, waste-list-retrieving and score-retrieving

- [x] Add weight property to wastes

- [ ] Update the "score" field in User class on demand

- [x] User log in authorization

- [x] Incorrect waste-sorting tag for user

  ......



## HTTP request examples (only for test purposes):

1. Add a user.

   - Use `HTTP POST` method at `/add-user`

      | KEY  |          VALUE          |        DESCRIPTION         |
      | :--: | :---------------------: | :------------------------: |
      |  id  |         Integer         | The user's identification. |
      | name | String (defaults to "") |      The user's name.      |
     
   - Duplicated users will cause `409 Conflict`.
   
2. Check the user credentials.

   - Use `HTTP GET` method at `/get-user`

      | KEY  | VALUE   | DESCRIPTION                       |
      | ---- | ------- | --------------------------------- |
      | id   | Integer | The user's identification number. |

   - Returns the name of the user.

   - An ID that does not exist will cause `404 Not Found`.

3. Add waste to a user.

   - Use `HTTP POST` method at `/add-waste`

      |    KEY    |        VALUE         |                         DESCRIPTION                          |
      | :-------: | :------------------: | :----------------------------------------------------------: |
      |    id     |       Integer        |            The user's ID that the waste longs to.            |
      | category  |     Enumeration      | The category of the waste. Possible values are `HAZARDOUS_WASTE`,  `RECYCLABLE_WASTE`, ` FOOD_WASTE` and `RESIDUAL_WASTE`. |
      |  weight   | Floating Point Value |                  The weight of the rubbish.                  |
      | dustbinid |       Integer        |             The corresponding ID of the dustbin.             |
      |   time    |        String        | Submission time in the format of "yyyy-MM-dd HH:mm:ss". Example: "2012-02-01 12:01:02". |
     
   - A user ID or dustbin ID that does not exist will cause `404 Not Found`.

4. Retrieve a list of thrown wastes.

   - Use `HTTP GET` method at `/get-waste-list-top20`

   - Use `HTTP GET` method at `/get-waste-list-all`

     | KEY  |  VALUE  |             DESCRIPTION             |
     | :--: | :-----: | :---------------------------------: |
     |  id  | Integer | The user that the wastes belong to. |
     
   - The required list will be returned in JSON ordered by internally generated ID descendingly.

       ```json
       [
           {
               "category": "RESIDUAL_WASTE",
               "correctlyCategorized": true,
               "dustbin": {
                   "full": false,
                   "id": 1,
                   "latitude": 40.1564221,
                   "longitude": 116.283188,
                   "name": "BUPT S"
               },
               "id": 5,
               "time": "2020-12-14T14:10:10",
               "weight": 0.98
           }
       ]
       ```

   - A user ID that does not exist will cause `404 Not Found`.

5. Retrieve the credit of a user (not implemented).

   - Use `HTTP GET` method at `/get-credit`

     | KEY  |  VALUE  |             DESCRIPTION             |
     | :--: | :-----: | :---------------------------------: |
     |  id  | Integer | The user that the score belongs to. |
     
   - A user ID that does not exist will cause `404 Not Found`.

6. Report an incorrect categorization.

   - Use `HTTP POST` method at `"/report-incorrect-categorization"`
      |    KEY    |  VALUE  |                         DESCRIPTION                          |
      | :-------: | :-----: | :----------------------------------------------------------: |
      | dustbinid | Integer |             The corresponding ID of the dustbin.             |
      |   time    | String  | Submission time in the format of "yyyy-MM-dd HH:mm:ss". Example: "2012-02-01 12:01:02". |

   - Returns a message that describes which waste is marked.

       `wasteId=4, wasteTime=2020-12-13T10:18:10, userId=2019211915, userName=Charlie Chiang marked.`

   - A dustbin ID that does not exist will cause `404 Not Found`.

7. Add a dustbin.

   - Use `HTTP POST` method at `/add-dustbin`

     |    KEY    |        VALUE         |        DESCRIPTION        |
     | :-------: | :------------------: | :-----------------------: |
     |   name    |        String        |   Name of the dustbin.    |
     | latitude  | Floating Point Value | Latitude of the dustbin.  |
     | longitude | Floating Point Value | Longitude of the dustbin. |
     
   - Returns the generated ID of the created dustbin.
   
8. Retrieve a list of dustbins.

    - Use `HTTP GET` method at `/get-dustbin-list`

    |  KEY   | VALUE | DESCRIPTION |
    | :----: | :---: | :---------: |
    | (null) |       |             |
    
    - The required list will be returned in JSON.
    
       ```json
       [
           {
               "full": false,
               "id": 1,
               "latitude": 40.1564221,
               "longitude": 116.283188,
               "name": "BUPT S"
           }
       ]
       ```
   
       

## How to run this project

1. Clone this repository

   `https://github.com/charlie0129/waste-sorting-helper-server.git`

2. Create a MySQL database

   ```mysql
   create database waste_sorting_helper_db;
   create user 'waste_sorting_helper_admin'@'%' identified by 'your_password';
   grant all on waste_sorting_helper_db.* to 'waste_sorting_helper_admin'@'%';
   ```

3. Run this project

   `mvn spring-boot:run`

   Test the HTTP requests at port `8080`.
   
4. (Optional) Test the APIs (see `api-test.sh`).

    ```shell
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
    
    ```

    

