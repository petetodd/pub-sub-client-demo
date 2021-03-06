package com.brightbluecircle.Subscription;

import com.google.api.services.pubsub.Pubsub;
import com.google.api.services.pubsub.model.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Created by peter on 21/05/2017.
 */
public class StuttSub {

    /**
     * Pull batch size.
     */
    static final int BATCH_SIZE = 1000;

    /**
     * A name of environment variable for decide whether or not to loop.
     */
    static final String LOOP_ENV_NAME = "LOOP";

    public String testMessge(){
        System.err.println("testMessge");


        return "TEST";
    }


    /**
     * Creates a new subscription.
     *
    // * @param client Cloud Pub/Sub client.
    // * @param args Arguments as an array of String.
     * @throws IOException when Cloud Pub/Sub API calls fail.
     */
    /*
    public static void createSubscription()
            throws IOException {

        Pubsub client = PubsubUtils.getClient();


        String subscriptionName = PubsubUtils.getFullyQualifiedResourceName(
                PubsubUtils.ResourceType.SUBSCRIPTION, "stuttgart-pilot","stuttPull1Sub");

        Subscription subscription = new Subscription()
                .setTopic(PubsubUtils.getFullyQualifiedResourceName(
                        PubsubUtils.ResourceType.TOPIC, "stuttgart-pilot", "stuttPull1"));


        subscription = client.projects().subscriptions()
                .create(subscriptionName, subscription)
                .execute();
        System.out.printf(
                "Subscription %s was created.\n", subscription.getName());
        System.out.println(subscription.toPrettyString());
    }
*/

    /**
     * Keeps pulling messages from the given subscription.
     *
     * @throws IOException when Cloud Pub/Sub API calls fail.
     */
    public static void pullMessages()
            throws IOException {
        System.err.println("pullMessages ");
        System.err.println("subscriptionName ");



        String subscriptionName = PubsubUtils.getFullyQualifiedResourceName(
                PubsubUtils.ResourceType.SUBSCRIPTION, "stuttgart-pilot","productionsub");
        System.err.println("subscriptionName : " + subscriptionName);

        System.err.println("pullRequest ");

        PullRequest pullRequest = new PullRequest()
                .setReturnImmediately(false)
                .setMaxMessages(BATCH_SIZE);
        System.err.println("client ");

        Pubsub client = PubsubUtils.getClient();


        do {
            PullResponse pullResponse;
            pullResponse = client.projects().subscriptions()
                    .pull(subscriptionName, pullRequest)
                    .execute();
            List<String> ackIds = new ArrayList<>(BATCH_SIZE);
            List<ReceivedMessage> receivedMessages =
                    pullResponse.getReceivedMessages();
            if (receivedMessages != null) {
                for (ReceivedMessage receivedMessage : receivedMessages) {
                    PubsubMessage pubsubMessage =
                            receivedMessage.getMessage();
                    if (pubsubMessage != null
                            && pubsubMessage.decodeData() != null) {



                        String strMessage =  new String(pubsubMessage.decodeData(),
                                "UTF-8");

                        String[] stoneParams = new String[4];
                        stoneParams[0] = "python3";
                        stoneParams[1] = "stones.py";
                        stoneParams[2] = "/dev/ttyUSB0";
                        stoneParams[3] = strMessage;
                        // For Debug
                        String strMessageOut = "python3 stones.py /dev/ttyUSB0 '" + strMessage + "'";
                        System.out.println(strMessageOut);

                        // Start Py stone script
                        Runtime rt = Runtime.getRuntime();
                        try {
                            Process pr = rt.exec(stoneParams);
                            System.err.println("PYTHON TRIGGERED XX");
                            pr.waitFor();

                            BufferedReader reader =
                                    new BufferedReader(new InputStreamReader(pr.getInputStream()));

                            String line = "";
                            while ((line = reader.readLine())!= null) {
                                System.err.append(line + "\n");
                            }


                        } catch (Exception ex) {
                            ex.printStackTrace();
                            System.err.println(ex.getLocalizedMessage());
                        }

/*
                        System.out.println(
                                new String(pubsubMessage.decodeData(),
                                        "UTF-8"));

                        if (pubsubMessage.getAttributes() != null){
                            Map<String, String> map = pubsubMessage.getAttributes();
                            for (Map.Entry<String, String> entry : map.entrySet()) {
                              //  System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
                            }
                        }
                        */


                    }
                    ackIds.add(receivedMessage.getAckId());
                }
                AcknowledgeRequest ackRequest = new AcknowledgeRequest();
                ackRequest.setAckIds(ackIds);
                client.projects().subscriptions()
                        .acknowledge(subscriptionName, ackRequest)
                        .execute();
            }
           System.err.println("IN LOOP: " +LOOP_ENV_NAME);

        } while (LOOP_ENV_NAME != null);
        System.err.println("RESTART required: " +LOOP_ENV_NAME);
    }




}
