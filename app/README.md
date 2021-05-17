An App to interact with Cloud AI service for object detection in an image.

Cloud Provider : AWS

Functionality : The App detects objects in an image using AWS Image Rekognition AI service. To integrate with AWS, AWS Amplify Android have been used. Images can be picked either from gallery or captured through the phone's camera.

Steps involved:
-Setup a new project in Android Studio.
-Install Amplify CLI using npm install -g @aws-amplify/cli
-Then configure amplify by creating a user in AWS Management Console.
-Then setup with the access keys of the user.
-Add dependencies of amplify framework in build.gradle.
-Then Gradle Sync and initialize amplify by amplify init command and choose the profile and environment, IDE etc.
-To initialize amplify in the application, we have to add “Amplify.configure(getApplicationContext())” in onCreate() function.
-Build the project and amplify will be initialized.
-Amplify has certain plugins for the AWS Services. I have used Amplify predictions to detect labels from an image.
-Add the Prediction Plugin in Amplify. For that first through the command line we have to run “amplify add predictions” command.
-Add the required configurations through the command line.
-Then “amplify push” command will create the resources in the cloud.
-After that add the plugin in the project to call the API. For that first add the prediction dependencies in the build.gradle. Then add the plugin in the project by adding “Amplify.addPlugin(AWSPredictionsPlugin)” in onCreate() function
-Then we can use the predictions to find objects in an image by calling Amplify.Predictions.identify() to get the labels by passing a bitmap image.

Test cases observation:
For most of the images the objects were identified with an accuracy of 85-90%. When images were captured through phone and was not clear, the accuracy decreased. If the image is clear, the accuracy were higher.

References:
https://docs.amplify.aws/lib/predictions/label-image/q/platform/android#label-objects-in-an-image
https://docs.amplify.aws/lib/project-setup/create-application/q/platform/android
