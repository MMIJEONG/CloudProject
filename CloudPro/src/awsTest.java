import java.util.Scanner;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.*;


public class awsTest {
    /*
     * Cloud Computing, Data Computing Laboratory
     * Department of Computer Science
     * Chungbuk National University */
    static AmazonEC2 ec2;

    private static void init() throws Exception {
        /*
         * The ProfileCredentialsProvider will return your [default]
         * credential profile by reading from the credentials file located at * (~/.aws/credentials).
         */
        ProfileCredentialsProvider credentialsProvider = new ProfileCredentialsProvider();
        try {
            credentialsProvider.getCredentials();
        } catch (Exception e) {
            throw new AmazonClientException(
                    "Cannot load the credentials from the credential profiles file. " + "Please make sure that your credentials file is at the correct " + "location (~/.aws/credentials), and is in valid format.",
                    e);
        }
        ec2 = AmazonEC2ClientBuilder.standard()
                .withCredentials(credentialsProvider)
                .withRegion("us-east-2") /* check the region at AWS console */.build();
    }

    public static void main(String[] args) throws Exception {
        init();
        Scanner menu = new Scanner(System.in);
        Scanner id_string = new Scanner(System.in);
        int number = 0;
        while (true) {
            System.out.println("															");
            System.out.println("------------------------------------------------------------");
            System.out.println(" Amazon AWS Control Panel using SDK ");
            System.out.println("															");
            System.out.println(" Cloud Computing, Computer Science Department ");
            System.out.println("			at Chungbuk National University ");
            System.out.println("------------------------------------------------------------");
            System.out.println(" 1. list instance\t 2. available zones ");
            System.out.println(" 3. start instance\t 4. available regions ");
            System.out.println(" 5. stop instance\t 6. create instance ");
            System.out.println(" 7. reboot instance\t 8. list images ");
            System.out.println(" \t\t\t\t\t 99. quit");
            System.out.println("------------------------------------------------------------");

            System.out.print("Enter an integer: ");
            number=menu.nextInt();


            switch (number) {
                case 1:
                    listInstances();
                    break;
                case 2:
                    listAvailablezone();
                    break;
                case 3:
                    System.out.print("Enter instance id:");
                    String start_id=id_string.next();
                    startInstances(start_id);
                    break;
                case 4:
                    listAvailableregion();
                    break;
                case 5:
                    System.out.print("Enter instance id:");
                    String stop_id=id_string.next();
                    stopInstances(stop_id);
                    break;
                case 6:
                    System.out.print("Enter ami id:");
                    String ami_id=id_string.next();
                    createInstances(ami_id);
                    break;
                case 7:
                    System.out.print("Enter instance id:");
                    String reboot_id=id_string.next();
                    rebootInstances(reboot_id);
                    break;
                case 8:
                    listImages();
                    break;
                case 99:
                    System.exit(0);
            }
        }
    }


    public static void listInstances() {
        System.out.println("Listing instances....");
        boolean done = false;
        DescribeInstancesRequest request = new DescribeInstancesRequest();
        while (!done) {
            DescribeInstancesResult response = ec2.describeInstances(request);
            for (Reservation reservation : response.getReservations()) {
                for (Instance instance : reservation.getInstances()) {
                    System.out.printf("[id] %s, " +
                            "[AMI] %s, " +
                            "[type] %s, " +
                            "[state] %10s, " +
                            "[monitoring state] %s", instance.getInstanceId(), instance.getImageId(), instance.getInstanceType(), instance.getState().getName(), instance.getMonitoring().getState());
                }
                System.out.println();
            }
            request.setNextToken(response.getNextToken());
            if (response.getNextToken() == null) {
                done = true;
            }
        }
    }
    public static void startInstances(String instance_id) {
        System.out.printf("Starting ....%s\n",instance_id);
        final AmazonEC2 ec2 = AmazonEC2ClientBuilder.defaultClient();
        StartInstancesRequest request = new StartInstancesRequest().withInstanceIds(instance_id);
        ec2.startInstances(request);
        System.out.printf("Successfully started instance %s\n",instance_id);

    }
    public static void stopInstances(String instance_id) {
        final AmazonEC2 ec2 = AmazonEC2ClientBuilder.defaultClient();
        StopInstancesRequest request = new StopInstancesRequest().withInstanceIds(instance_id);
        ec2.stopInstances(request);
        System.out.printf("Successfully stop instance %s\n",instance_id);

    }
    public static void rebootInstances(String instance_id) {
        System.out.printf("Rebooting ....%s\n",instance_id);
        final AmazonEC2 ec2 = AmazonEC2ClientBuilder.defaultClient();
        RebootInstancesRequest request = new RebootInstancesRequest().withInstanceIds(instance_id);
        RebootInstancesResult response = ec2.rebootInstances(request);
        System.out.printf("Successfully rebooted instance %s\n",instance_id);

    }
    public static void listAvailablezone() {
        System.out.println("Available zones....");
        DescribeAvailabilityZonesResult zones_response = ec2.describeAvailabilityZones();
        int count=0;
        for(AvailabilityZone zone : zones_response.getAvailabilityZones()) {
            System.out.printf("[id] %s, " +
                    "[region] %s, " +
                    "[zone] %s ", zone.getZoneId(), zone.getRegionName(), zone.getZoneName());
            System.out.println();
            count+=1;
        }
        System.out.printf("You have access to %d Availability Zones.\n",count);
    }
    public static void listAvailableregion() {
        System.out.println("Available regions ....");
        DescribeRegionsResult regions_response = ec2.describeRegions();
        for(Region region : regions_response.getRegions()) {
            System.out.printf("[region] %s, " + "[endpoint] %s ", region.getRegionName(), region.getEndpoint());
            System.out.println();
        }
    }
    public static void listImages() {
        System.out.println("Listing images....");
        DescribeImagesRequest request = new DescribeImagesRequest().withOwners("AWS계정ID");
        DescribeImagesResult response = ec2.describeImages(request);
        for (Image image : response.getImages()) {
            System.out.printf("[ImageID] %s, " + "[Name] %s, " + "[Owner] %s ", image.getImageId(), image.getName(), image.getOwnerId());
            System.out.println();
        }

    }
    public static void createInstances(String ami_id) {
        RunInstancesRequest runInstancesRequest = new RunInstancesRequest();
        runInstancesRequest.withImageId(ami_id)
                           .withInstanceType(InstanceType.T2Micro)
                           .withMinCount(1)
                           .withMaxCount(1)
                           .withKeyName("my-key-pair")
                           .withSecurityGroups("my-security-group");
        RunInstancesResult response = ec2.runInstances(runInstancesRequest);
        String new_instance_id = response.getReservation().getInstances().get(0).getInstanceId();
        System.out.printf("Successfully started EC2 instance %s based on AMI %s",new_instance_id,ami_id);

    }

}
