package com.brightbluecircle.Subscription;

import com.google.api.services.pubsub.Pubsub;
import com.google.api.services.pubsub.model.*;

import java.io.IOException;
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
        System.out.println("testMessge");


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
        System.out.println("pullMessages ");
        System.out.println("subscriptionName ");



        String subscriptionName = PubsubUtils.getFullyQualifiedResourceName(
                PubsubUtils.ResourceType.SUBSCRIPTION, "stuttgart-pilot","stuttPull1Sub");
        System.out.println("subscriptionName : " + subscriptionName);

        System.out.println("pullRequest ");

        PullRequest pullRequest = new PullRequest()
                .setReturnImmediately(false)
                .setMaxMessages(BATCH_SIZE);
        System.out.println("client ");

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
                        System.out.println(
                                new String(pubsubMessage.decodeData(),
                                        "UTF-8"));

                        if (pubsubMessage.getAttributes() != null){
                            Map<String, String> map = pubsubMessage.getAttributes();
                            for (Map.Entry<String, String> entry : map.entrySet()) {
                                System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
                            }
                        }


                    }
                    ackIds.add(receivedMessage.getAckId());
                }
                AcknowledgeRequest ackRequest = new AcknowledgeRequest();
                ackRequest.setAckIds(ackIds);
                client.projects().subscriptions()
                        .acknowledge(subscriptionName, ackRequest)
                        .execute();
            }
            System.out.println("IN LOOP: " +LOOP_ENV_NAME);

        } while (LOOP_ENV_NAME != null);
        System.out.println("OUT LOOP: " +LOOP_ENV_NAME);
    }




}
