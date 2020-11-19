# waste-sorting-helper-server (dev)

Back end for project "Research on campus garbage sorting and Recycling".

See also: [waste-sorting-helper-weapp](https://github.com/charlie0129/waste-sorting-helper-weapp).

*All APIs are for test purposes.*

**This project is still in its alpha stage,  under development. There is no guarantee that the program can run without problems.**

 TODO:

- [x] Basic features including user-addition, waste-addition, waste-list-retrieving and score-retrieving

- [ ] Add weight property to wastes

- [ ] Update the "score" field in User class on demand

- [ ] User log in authorization

- [ ] Administrator features, such as waste counting.

- [ ] Incorrect waste-sorting tag for user

  ......



## HTTP request examples (only for test purposes):

1. Add a user.

   - Use `HTTP POST` method at `/add-user`

     | KEY  |      VALUE       |            DESCRIPTION            |
     | :--: | :--------------: | :-------------------------------: |
     |  id  |                  | The user's identification number. |
     | name | (defaults to "") |         The user's name.          |
   
2. Add waste to a user.

   - Use `HTTP POST` method at `/add-waste`

     |   KEY    | VALUE |                         DESCRIPTION                          |
     | :------: | :---: | :----------------------------------------------------------: |
     |    id    |       |              The user that the waste longs to.               |
     | category |       | The category of the waste. Possible values are `HAZARDOUS_WASTE`,  `RECYCLABLE_WASTE`, ` FOOD_WASTE` and `RESIDUAL_WASTE`. |

3. Retrieve a list of thrown wastes.
   - Use `HTTP GET` method at `/get-waste-list`

     | KEY  | VALUE |             DESCRIPTION             |
     | :--: | :---: | :---------------------------------: |
     |  id  |       | The user that the wastes belong to. |
     
   - The required messages will be returned in JSON format.
   
4. Retrieve the score of a user.

   - Use `HTTP GET` method at `/get-score`

     | KEY  | VALUE |             DESCRIPTION             |
     | :--: | :---: | :---------------------------------: |
     |  id  |       | The user that the score belongs to. |
     



## How to run this project

1. Clone this repository

   `https://github.com/charlie0129/waste-sorting-helper-server.git`

2. Create a MySQL database

   ```mssql
   create database waste_sorting_helper_db
   create user 'administrator'@'%' identified by 'pswd'
   grant all on waste_sorting_helper_db.* to 'administrator'@'%'
   ```

3. Run this project

   `mvn spring-boot:run`

   Test the HTTP requests at port `8080`.