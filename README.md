# waste-sorting-helper-server (dev)

Back end for project "Research on campus garbage sorting and Recycling".

See also: [waste-sorting-helper-weapp](https://github.com/charlie0129/waste-sorting-helper-weapp).

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
   
2. Check the user credentials.

   - Use `HTTP GET` method at `/get-user`

       | KEY  | VALUE   | DESCRIPTION                       |
       | ---- | ------- | --------------------------------- |
       | id   | Integer | The user's identification number. |

   - Returns the name of the user, or `404 Not Found` if the user does not exist.

3. Add waste to a user.

   - Use `HTTP POST` method at `/add-waste`

     |    KEY    |        VALUE         |                         DESCRIPTION                          |
     | :-------: | :------------------: | :----------------------------------------------------------: |
     |    id     |       Integer        |            The user's ID that the waste longs to.            |
     | category  |     Enumeration      | The category of the waste. Possible values are `HAZARDOUS_WASTE`,  `RECYCLABLE_WASTE`, ` FOOD_WASTE` and `RESIDUAL_WASTE`. |
     |  weight   | Floating Point Value |                  The weight of the rubbish.                  |
     | dustbinid |       Integer        |             The corresponding ID of the dustbin.             |
     |   time    |        String        | Submission time in the format of "yyyy-MM-dd HH:mm:ss". Example: "2012-02-01 12:01:02". |

4. Retrieve a list of thrown wastes.

   - Use `HTTP GET` method at `/get-waste-list-top20`

   - Use `HTTP GET` method at `/get-waste-list-all`

     | KEY  |  VALUE  |             DESCRIPTION             |
     | :--: | :-----: | :---------------------------------: |
     |  id  | Integer | The user that the wastes belong to. |
     
   - The required messages will be returned in JSON format.

5. Retrieve the credit of a user.

   - Use `HTTP GET` method at `/get-credit`

     | KEY  |  VALUE  |             DESCRIPTION             |
     | :--: | :-----: | :---------------------------------: |
     |  id  | Integer | The user that the score belongs to. |
     

6. Report an incorrect categorization.

    - Use `HTTP POST` method at `"/report-incorrect-categorization"`

        |    KEY    |  VALUE  |                         DESCRIPTION                          |
        | :-------: | :-----: | :----------------------------------------------------------: |
        | dustbinid | Integer |             The corresponding ID of the dustbin.             |
        |   time    | String  | Submission time in the format of "yyyy-MM-dd HH:mm:ss". Example: "2012-02-01 12:01:02". |

        

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