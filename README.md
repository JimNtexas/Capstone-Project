
https://stackedit.io/editor#

**Electoral College Calculator v1**

*Preface*:  
Way back in 1995 I was driving to work and heard a person on the radio discussing the 1996 presidential election between Bill Clinton and Bob Dole say ‘Dole can’t win because he can’t carry California’.
At that time I was working on something called a ‘web browser’, a tool for using this new http protocol. I figured that at least one of the 23,500 web sites then in existence would include a calculator of some sort for gaming the Electoral College System.
There wasn’t, so I had to invent one. Our company was experimenting with a new language called ‘Java’, that was then in Beta. I got permission to use some of my time to develop a Java Applet for this purpose. I worked on this app for several years, it supported all U.S. Presidential elections from 1789 through 2008. Users could load a historic election and change the results to explore different possible outcomes. The applet is still available at http://grayraven.com/ec2/ , but no current web browsers support this old Java applet.

The purpose of this, my Udacity Capstone project, is to create an Android application that will allow users to review historical elections, and to create their own projections.

*Introduction*:  The calculator can be used to review past election results, and to model possible outcomes of the 2016 Presidential election.

There are four main user interface screens.

1) Login:

![enter image description here](https://lh3.googleusercontent.com/3nneDqNZ6ACbzXDBnDSaHDq8-6CGe9jN2PzmVRzysQ2-pACBhoQ-ajo3AUWXgtI4iSAVo6f4uZ9-0zr6igJXIuaZvh1ZISwxFFjoB6Q8ba8FtHezmo9d-BnNU71_ef0fJ-UrxIyJt7O75BtsxxrLDE3IyoFOkEB0pUNOP_LxP4iFzucFunjN_tIlKDUhfppMZeqROrrTO_nOeyHfD1j-lw5-yV0qOjlf48NijmYPuHsQi6XDAtPJ7e271vNgoqfSp2KgzvVpPaaLrCWbtcyTCa25qxrARQctBn4oDWwYiSVB4rJMTE1oR1qt5Vlt9Pbnx6D-iU5_B7xhC4zZ5m9Qpu3k9S9Lk871uv-3laIH5n3CawJI-Tn0wj15L5NNkrVGqfOLsGWd5QbE2oFJCzUw71lcMN5oz4KrNiy7kBIelZsafP3CnDtcAlEDb7soXsUAWmWO1NPhy12U28GrIYel4yrc6lzqpzCIM8Ww2_ktmOt9SUS4JHIes4Lpx7Ji1EsQKsuUZIuGhafHtj7-OwBmwswN9qrB601C-dSb-tO2wjrhYvEIKU4esEV-pfVm4XIbdkvri6jxB067haI5vUb4hBu7GEe_8woh=w270-h480-no)

Users may login using their Google account, or users can create an account for the Calculator's Firebase database by selecting the corresponding field.

Once a user is logged in then Firebase will persist a local copy of the data, useable in the event of poor or no network conditions. While logged in  the local database will synchronize with that of the Firebase server once connectivity has been restored. 

Logged out users will have to wait for internet connectivity in order to log in.  


*Main Screen*:

![enter image description here](https://lh3.googleusercontent.com/yYMSqmplYDsoHVPy1VkqHUZNgLtBTwd030GAHiIyNnJjXgBq7_QcjHjNJ_8eq-XSodw3erIwMe4N2W5xl6PBPm-zKKl1jq4RVpy4AJ4HNe5qI8wXO_Syv-XwExNP_buRgbE6GNqKMfN-2Sf1SRJkpwAqOz5XBLQq-fXuD3Qh3egYZe4RItCpz26DIiJUh8CzJ3n_1HiE5c7F14NK8Z01n6mIxj_7Nty53YFjc7GCYW_I5vT2PAq4crlG2ggAxBds29_sTClqE4EtY4wAQeJ0LGCbmw29MTxu7KiiSxWDQT_q6Hbvthe4Vdajhw7mR-d9wB0SvcDJQccHaemi42EzaCkqpGHhPZ_bHkxQ8oD8mzobqefFSinqqiaKswPGbMjisZRr58rHvXMLlKNBJURAvbnVKuk2kEetbXZk36G7zgJAjd7X9CCybnvTMCgrwPNZRN7XBLSvtXbXLE0lb7HxUXrYPC6DHZkpanGUINVNxZpM6EDcCfqbSG4J3wEWfONEXjst2GOKGMJ7Mked9IM48casNbz46ecdJYvIksH7vjQ8HmArkSGSqzdVfVqXY2U-4xPmcw3kVWh5_S5BSM9rc91oJJ6S0wOy=w270-h480-no)

The main screen is used to display saved elections, and to select elections for display in the detail view.

The "official" elections contain the historical results of past elections.  These are provided to all application users and may not be modified or deleted.  Copies of these elections can be saved.

User may tap on an entry in the list to view the details for that election.

Users create elections by tapping the floating action button.   The app will load the detail screen and display a blank election.  Users may also save any election using a different title.

User created elections may be deleted by tapping on the trash can icon.

The options menu on the main view allows users to log out of the app, or view a list of historical elections. 

The application will occasionally display an interstitial double click add when transitioning from the main view to the detail view. 

*Election Detail Screen*: 

![enter image description here](https://lh3.googleusercontent.com/nJ8Jrq9KU-aeSxbEqN9nJ0EoH1LE4tmyFfUO74Qi1vANT0m2oJCX-DOG_C9ZlfmDSR4qv1UjvHisodXEfHZoIYYePF6lkwfde1vV98WfM_JmKVZWfe0_riaMhvpquwbQ532L23C1fOBpeg-yZ7Iea1zMG7xSNhyiNKAjdzEBy9jz2qDW2Gh8YuoTk9IPE64ErAtKSat6CHx2c88YBvJExebCcSM2Qd3FR_R0RJubWJNdimUH6hhvjy82o3bbtc3kY3zvN3YIma29LdWks__XUGOAFv7sCiD6VfJqAFsmKKpYG8eGn0MJy-RHtpBYzNz3ebm_6WgIoSaGsfWv_COlYsQ5bPK1uVflB3HCSRmZYA6fSQ3ZVQFIIJexQdWLRa1nsvN0FKQ12ac6hRWceguoGCrPXbToXCWfVyj6y3tSspRm4VemX1VM_9-CfwqfDNlQJuVYInxqp42dJZ2lg6oY-LoWQ6y5plRb7SpmF-__lg30lQATtQQdxby7Vw1sGUfy4UCpx-6icPh0rpfUgoHvh3xRdI69l3LNxb36ocajNc-zeK7-Eg9Hq7cj3e6HGIbbq14SvrxuoxGldYw0Codf-RIG8nYkODsA=w270-h480-no)

The numbers in the 'State' column are that state's allocation of electoral college votes.  When a party letter is clicked on in the Democratic or Republican columns, that cell turns blue or red, and the stats votes are added to the totals displayed in the top of the view.

When one party's votes equal or exceed 270, that vote total turns green, signifying a winner.

Most states are 'winner take all', but Maine and Nebraska are special cases. They are allowed to split their votes between both parties.  When those states split their votes (as happened in 2008) their cells turn purples, and the cells display the number of votes allocated for each party.

Users can split votes for these states by clicking on the word 'Split' in the right column.

Users can save their results to their Firebase database with the 'save' button.   If desired, users can click on the 'year' text field to select a different election year for this file.  This feature is helpful for those wishing to initialize their grid with a past election.

*Historical Election Screen*:

![enter image description here](https://lh3.googleusercontent.com/qrjA_gqPeowSgRGugY8ur_eerpLGRv3WKHZkCf65FLWeEoPg8XjFJNyAD5vSQFo4BhBTTJ5I0EVMuMGokRZdy_J0iYHEVqKijwMy9akDX0ExBrgq174qrYDDR7PDA565gwkN-TY5Y9yYUz8Xcv7pS59GMT4oJ6x8uQtJRVEtFVs6hQBLAraFsJcgIlaDHZXyWt_lXmr1VkYSFAj2h8H-sA58rqoGwcIlFIxjOY5FbFWpUEB5m0bvbJupRoZtvFARHkYQVCE2_ILwlaLs-Tm02A6YQ708CZvlry0_3giSNUvQYbenJ4JwdPsSno2b_1ZWN7HvnqXaDq16eIovgGjSWeLXXhbkCo5eT6f6TpRnFpfZnpK_9IqUw-9vppAkfUQvMJ6v-JzO0osO_y5_z_R9E7xUD_EpDaGOC-xKEIbsb3F0XM0xKKx5pbh6uunvF2qAQu4D56je5spGmb-4jZjlo4J8e3cKH0JLH3woIq6IDjEMWglcsmDA5NCIYBK9snVhvPr_uZw_shXiHApnh7a1e_9-vIiPgyq7jUTFjbYvrALt_yI1XxctwQzOQO3lIF-P3VgytxyTbVEKPayiZixKtQ213zjpgnjx=w270-h480-no)

Users desiring more information about past elections can display the Election History screen from the options menu on the main screen.  

This screen displays a list of past elections. These elections are downloaded at application start and saved in the device's Sqlite database using an intent service and content provider, and displayed via a loader.   

Clicking on a historical election will open the Wikipedia page corresponding to that election.

*Code details*

This application uses the third party libraries GreenRobot EventBus, Butterknife, and Google Gson.

User input and database fields are sanitized for your protection.

Support for accessibility is included in the application. 

All strings are contained in strings.xml.  Strings are formatted for RTL and marked non-translatable as required.  

The app provides a Widget that displays the number of days until Election Day.

The app used Firebase database, Firebase Authentication for username/password and Google Oauth.  It also incorporates Google/Doubleclick advertising.

The Android Studio project will build a clean, signed release version of the application.  The builder must supply a google-services.json file in the Capstone-Project\ElectoralCalc\app folder.   See the Firebase documentation for how to create this file.



