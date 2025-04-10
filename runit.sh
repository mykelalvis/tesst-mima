export KLASSPATH=$(mvn  dependency:build-classpath | grep m2):target/classes
java -cp $KLASSPATH testme.TrialRun
