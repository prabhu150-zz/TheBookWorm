# TheBookWorm
Ecommerce Android application for buying and selling of books

Team:
Abhijeet Prabhu 823323196
Shubham Kale 822707841

Brief description of the app:

Its an ecommerce app that supports the buying and selling of books online for
independent bookstore owners and other shopkeepers who want to sell their books online.


Features of the app:

1. Supports multiple buyer/seller login
2. Buyer has the ability to browse through the catalog and order products that are offered by different retailers
3. Buyer has the ability to see his previous orders ie number of items bought,money spent on tax/shipping
4. Buyer can add/remove items from cart dynamically when browsing through their catalog
5. Seller can bulk add all their books directly from an excel file. Since the column names directly affect database schema,
I have kept the files in the assets folder. 3 seperate files for 3 different sellers.
6. Seller can view/delete/modify the items in their inventory
7. Seller can see who their customers are and how many products they have bought from them. They can see how much money they
have spent on shipping/tax for just their items in the user's aggregated order and see their revenue.


Dependencies:

App uses androidX. Hence it may include several components that I didnt directly account for. Therefore,including a copy from my gradle below:

**********************************************************************************

testImplementation 'junit:junit:4.12'
    androidTestImplementation 'androidx.test:runner:1.2.0-beta01'
    androidTestImplementation 'androidx.test.espresso:espresso-core:3.2.0-beta01'

    implementation 'androidx.exifinterface:exifinterface:1.1.0-alpha01'
    implementation 'androidx.media:media:1.1.0-beta01'
    implementation 'androidx.legacy:legacy-support-v4:1.0.0'
    implementation 'androidx.core:core:1.2.0-alpha01'
    implementation 'androidx.legacy:legacy-support-core-utils:1.0.0'
    implementation 'androidx.legacy:legacy-support-core-ui:1.0.0'
    implementation 'com.google.android.material:material:1.1.0-alpha06'

    implementation 'androidx.constraintlayout:constraintlayout:1.1.3'

    implementation 'com.google.firebase:firebase-core:16.0.9'
    implementation 'com.google.firebase:firebase-auth:17.0.0'
    implementation 'com.google.firebase:firebase-database:17.0.0'
    implementation 'com.google.firebase:firebase-storage:17.0.0'

    implementation 'com.squareup.picasso:picasso:2.71828'
    implementation 'androidx.cardview:cardview:1.0.0'
    implementation 'androidx.lifecycle:lifecycle-extensions:2.2.0-alpha01'
    implementation 'androidx.lifecycle:lifecycle-viewmodel-ktx:2.0.0'
    implementation 'androidx.appcompat:appcompat:1.1.0-alpha05'

    implementation 'de.hdodenhof:circleimageview:3.0.0'
    implementation 'com.xwray:groupie:2.3.0'
    implementation 'com.xwray:groupie-kotlin-android-extensions:2.3.0'
    implementation 'androidx.recyclerview:recyclerview:1.0.0'
    implementation 'org.apache.poi:poi:3.9'
    implementation 'io.paperdb:paperdb:2.6'

    implementation 'com.stripe:stripe-android:9.0.0'
    implementation 'com.basgeekball:awesome-validation:4.2'
    implementation 'com.stripe:stripe-android:9.0.0'
    implementation 'com.uncopt:android.justified:1.0'



**********************************************************************************



This app heavily relies on Paperdb and Groupie for persistent storage and 
recyclerview aggregation respectively. I hope those libraries work on your system.

Have used CircleImageView and Picasso to get attractive profile pictures to show. 


Instructions:

You can try with the below accounts to get started. To change any products to observe the changes modify the Books.xls
to add/modify/remove a product. Logging in as a seller and loading inventory will cause those changes to be reflected.

Please keep the columns intact. If I gave the option for uploading the file sometimes, the column data got corrupted which was
why keeping it in assets was the best choice I had.


Logging in as buyer gives you access to add/remove items from cart. Buy books and view past purchases.
Logging in as seller gives you access to your inventory and purchases users made from your shop. 

Password for all accounts is: 123456

Buyer logins:

jeff@gm.com
bill@gm.com
mark@gm.com

Seller logins:

wallmart@gm.com
target@gm.com
amz@gm.com




Issues:

Sometimes Picassos acts up especially when loading multiple products for the first time
or when a user has registered for the very first time. Usually logging out and logging back in
loads the images back. I have kept placeholder images to prevent any UI deformations.



