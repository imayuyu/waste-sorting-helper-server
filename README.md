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



## HTTP request specifications:

Most APIs are [`RESTful`](https://en.wikipedia.org/wiki/Representational_state_transfer) APIs like this `http://[host]:[port]/{service name}/{resource}`.

### Dustbin related APIs

| Resources                          | POST                             | GET                                | PUT               | DELETE             |
| ---------------------------------- | -------------------------------- | ---------------------------------- | ----------------- | ------------------ |
| `/api/dustbins`                    | Add a dustbin                    | List dustbins                      | -                 | -                  |
| `/api/dustbins/{dustbinId}`        | -                                | Identify the dustbin               | Replace a dustbin | Delete the dustbin |
| `/api/dustbins/{dustbinId}/full`   | Mark whether the dustbin is full | -                                  | -                 | -                  |
| `/api/dustbins/{dustbinId}/wastes` | -                                | List wastes related to the dustbin | -                 | -                  |

### User related APIs

| Resources                    | POST       | GET                             | PUT            | DELETE          |
| ---------------------------- | ---------- | ------------------------------- | -------------- | --------------- |
| `/api/users`                 | Add a user | -                               | -              | -               |
| `/api/users/{userId}`        | -          | Identify the user               | Replace a user | Delete the user |
| `/api/users/{userId}/wastes` | -          | List wastes related to the user | -              | -               |

### Waste related APIs

| Resources                                             | POST                               | GET                | PUT  | DELETE |
| ----------------------------------------------------- | ---------------------------------- | ------------------ | ---- | ------ |
| `/api/wastes`                                         | Add a waste                        | List wastes        | -    | -      |
| `/api/wastes/{wasteId}`                               | -                                  | Identify the waste | -    | -      |
| `/api/wastes/actions/report-incorrect-categorization` | Report an incorrect categorization | -                  | -    | -      |

### Details

- `POST /api/dustbins`: Add a dustbin

    - Request body is in JSON

    - Required JSON data fields

        | KEY       | TYPE    | DESCRIPTION |
        | --------- | ------- | ----------- |
        | name      | String  |             |
        | latitude  | Number  |             |
        | longitude | Number  |             |
        | isFull    | Boolean |             |

    - Request body example

        ```json
        {
          "name": "Dustbin Name",
          "latitude": 40.40,
          "longitude": 116.62,
          "isFull": false
        }
        ```

    - Response body example

        ```json
        {
          "id": 29,
          "name": "Dustbin Name",
          "latitude": 40.4,
          "longitude": 116.62,
          "full": null,
          "_links": {
            "self": {
              "href": "http://localhost:8080/api/dustbins/29"
            },
            "dustbins": {
              "href": "http://localhost:8080/api/dustbins"
            }
          }
        }
        ```

        

- `POST /api/dustbins/{dustbinId}/full`: Mark whether the dustbin is full
  
    - Required URL parameters
    
        | KEY    | TYPE    | DESCRIPTION |
        | ------ | ------- | ----------- |
        | isfull | boolean |             |
        

- `POST /api/users`: Add a user

    - Request body is in JSON

    - Required JSON data fields

        | KEY  | TYPE   | DESCRIPTION |
        | ---- | ------ | ----------- |
        | id   | Number |             |
        | name | String |             |

    - Request body example

        ```json
        {
          "name": "User Name",
          "id": 233
        }
        ```

    - Response body example

        ```json
        {
          "id": 233,
          "name": "User Name",
          "score": 0,
          "_links": {
            "self": {
              "href": "http://localhost:8080/api/users/233"
            }
          }
        }
        ```

- `POST /api/wastes`: Add a waste

    - Request body is in JSON

    - Required JSON data fields

        | KEY                    | TYPE    | DESCRIPTION                                                  |
        | ---------------------- | ------- | ------------------------------------------------------------ |
        | userId                 | Number  |                                                              |
        | dustbinId              | Number  |                                                              |
        | weight                 | Number  |                                                              |
        | category               | String  | The category of the waste. Possible values are `HAZARDOUS_WASTE`,  `RECYCLABLE_WASTE`, ` FOOD_WASTE` and `RESIDUAL_WASTE`. |
        | time                   | String  | Submission time in the format of "yyyy-MM-dd HH:mm:ss". Example: "2012-02-01 12:01:02". *(This field can be omitted)* |
        | isCorrectlyCategorized | Boolean |                                                              |

    - Request body example

        ```json
        {
          "userId": "233",
          "dustbinId": 28,
          "weight": 0.23,
          "category": "RECYCLABLE_WASTE",
          "time": "2011-11-11 11:11:11",
          "isCorrectlyCategorized": true
        }
        // or
        {
          "userId": "233",
          "dustbinId": 28,
          "weight": 0.54,
          "category": "RECYCLABLE_WASTE",
          "isCorrectlyCategorized": true
        }
        ```

    - Response body example

        ```json
        {
          "id": 30,
          "category": "RECYCLABLE_WASTE",
          "time": "2011-11-11T11:11:11",
          "weight": 0.23,
          "dustbin": {
            "id": 28,
            "name": "Wuxi",
            "latitude": 31.58844,
            "longitude": 120.35756,
            "full": false
          },
          "correctlyCategorized": true,
          "_links": {
            "self": {
              "href": "http://localhost:8080/api/wastes/30"
            }
          }
        }
        ```

- `POST /api/wastes/actions/report-incorrect-categorization`: Report an incorrect categorization

    - Required URL parameters

        | KEY       | TYPE   | DESCRIPTION                                                  |
        | --------- | ------ | ------------------------------------------------------------ |
        | dustbinid | Number |                                                              |
        | time      | String | Submission time in the format of "yyyy-MM-dd HH:mm:ss". Example: "2012-02-01 12:01:02". Waste logs earlier than this time will be evaluated. |

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
   
