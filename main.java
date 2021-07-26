import java.util.Scanner;

public class main {
    public static final String ANSI_RESET = "\033[0m";
    public static final String ANSI_BRIGHT_BLACK  = "\u001B[90m";
    public static final String ANSI_BRIGHT_RED    = "\u001B[91m";
    public static final String ANSI_BRIGHT_GREEN  = "\u001B[92m";
    public static final String ANSI_BRIGHT_YELLOW = "\u001B[93m";
    public static final String ANSI_BRIGHT_BLUE   = "\u001B[94m";
    public static final String ANSI_BRIGHT_PURPLE = "\u001B[95m";
    public static final String ANSI_BRIGHT_CYAN   = "\u001B[96m";
    public static final String ANSI_BRIGHT_WHITE  = "\u001B[97m";

    public static boolean nieJeCislo(String input) {
        if (input == null) {
            return true;
        }
        try {
            int d = Integer.parseInt(input);
        } catch (NumberFormatException nfe) {
            return true;
        }
        return false;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        int [][] finalAdd = new int[6][];

        //Getting input
        String in = "ipv4";
        /*while (true){
            System.out.print("Do you want IPv4 or IPv6 ? (write) : ");
            in = sc.nextLine().replaceAll(" +","");
            in = in.toLowerCase();
            if (in.equals("ipv4") || in.equals("ipv6")) break;
        }*/


        if (in.equals("ipv4")){//Process for IPv4
            String input = "";
            String [] address;

            while (true){//Getting address
                System.out.print("Write IP address without subnet mask : ");
                input = sc.nextLine();
                address = input.split("\\.");
                if (address.length == 4) {//Watching if all inputs are numbers
                    int count = 0;
                    for (int i = 0; i < 4; i++) {
                        if (!nieJeCislo(address[i]) && Integer.parseInt(address[i]) >= 0 && Integer.parseInt(address[i]) < 255) count++;
                    }
                    if (count == 4) break;
                    else System.out.println("You are stupid, try again");
                }
            }
            while (true){//Getting subnet mask
                System.out.print("Write Subnet mask : ");
                input = sc.nextLine().replaceAll(" +","");
                if (Integer.parseInt(input) > 0 && Integer.parseInt(input) < 32) break;
            }

            int subnet = Integer.parseInt(input);
            //Finding right spot
            int spot = 0;
            if (subnet > 0 && subnet <= 7) spot = 0;
            if (subnet >= 8 && subnet <= 15) spot = 1;
            if (subnet >= 16 && subnet <= 23) spot = 2;
            if (subnet >= 24 && subnet < 32) spot = 3;


            //getting binary number
            int ad = Integer.parseInt(address[spot]);
            subnet = (subnet%8)+1;
            String bin = "";
            int a = 256;
            for (int i = 0; i < 8; i++) {
                if (ad-(a/2) >= 0){
                    a = a / 2;
                    ad = ad - a;
                    bin +="1";
                }
                else {
                    bin += "0";
                    a = a/2;
                }
            }
            //Getting network
            a = 256;
            int first = 0;
            for (int i = 0; i < subnet-1; i++) {
                a = a/2;
                if (bin.charAt(i) == '1'){
                    first += a;
                }
            }

            //first row will be network
            finalAdd[0] = new int[4];

            for (int i = 0; i < 4; i++) {
                if (i < spot) {
                    finalAdd[0][i] = Integer.parseInt(address[i]);
                }
                if (i == spot) {
                    finalAdd[0][i] = first;
                }
                if (i > spot) {
                    finalAdd[0][i] = 0;
                }
            }

            //Getting broadcast
            bin = bin.substring(subnet);
            int last = 0;
            a = 1;
            for (int i = 0; i < 8-subnet; i++) {
                last += a*2;
                a = a*2;
            }

            //Second row will be broadcast
            finalAdd[1] = new int[4];

            for (int i = 0; i < 4; i++) {
                if (i < spot) {
                    finalAdd[1][i] = Integer.parseInt(address[i]);
                }
                if (i == spot) {
                    finalAdd[1][i] = (first+last+1);
                }
                if (i > spot) {
                    finalAdd[1][i] = 255;
                }
            }

            //Third row will be first host
            finalAdd[2] = new int[4];

            for (int i = 0; i < 4; i++) {
                if (i < 3){
                    finalAdd[2][i] = finalAdd[0][i];
                }
                else {
                    finalAdd[2][i] = (finalAdd[0][i]+1);
                }
            }

            //fourth row will be last host
            finalAdd[3] = new int[4];

            for (int i = 0; i < 4; i++) {
                if (i < 3){
                    finalAdd[3][i] = finalAdd[1][i];
                }
                else {
                    finalAdd[3][i] = (finalAdd[1][i]-1);
                }
            }

            //last row will be hosts
            finalAdd[4] = new int[1];

            int [] s = {1, 3, 7, 15, 31, 63, 127, 255};
            if (spot <3){
                int c = 256,sub = 0;
                if (spot == 0) c = 16777216;
                if (spot == 1) c = 65536;

                sub = c * s[bin.length()];


                if (spot == 2) sub +=254;
                if (spot == 1) sub +=65534;
                if (spot == 0) sub += 16777214;
                finalAdd[4][0] = sub;
            }
            else finalAdd[4][0] = last;

            //Fifth row will be subnet mask
            int [] subn= {255,254,252,248,240,224,192,128,0};
            finalAdd[5] = new int[4];

            for (int i = 0; i < 4; i++) {
                if (i < spot){
                    finalAdd[5][i] = 255;
                }
                if (i == spot){
                    finalAdd[5][i] = subn[bin.length()+1];
                }
                if (i > spot){
                    finalAdd[5][i] = 0;
                }
            }


            //printing results
            System.out.print(ANSI_BRIGHT_GREEN+"\nNetwork\t:\t");
            for (int j = 0; j < finalAdd[0].length; j++) {
                if (j == 3){
                    System.out.print(finalAdd[0][j]);
                }
                else {
                    System.out.print(finalAdd[0][j]+".");
                }
            }
            System.out.print(ANSI_BRIGHT_BLUE+"\nSubnet Mask\t:\t");
            for (int i = 0; i < 4; i++) {
                if (i == 3){
                    System.out.print(finalAdd[5][i]+ANSI_RESET);
                }
                else {
                    System.out.print(finalAdd[5][i]+".");
                }
            }
            System.out.print(ANSI_BRIGHT_CYAN+"\nBroadcast\t:\t");
            for (int j = 0; j < finalAdd[1].length; j++) {
                if (j == 3){
                    System.out.print(finalAdd[1][j]+ANSI_RESET);
                }
                else {
                    System.out.print(finalAdd[1][j]+".");
                }
            }
            System.out.print(ANSI_BRIGHT_YELLOW+"\nFirst / "+ANSI_RESET+ANSI_BRIGHT_PURPLE+"Last Host"+ANSI_RESET+ANSI_BRIGHT_YELLOW+"\t:\t");
            for (int j = 0; j < finalAdd[2].length; j++) {
                if (j == 3){
                    System.out.print(finalAdd[2][j]+ANSI_RESET);
                }
                else {
                    System.out.print(finalAdd[2][j]+".");
                }
            }
            System.out.print(ANSI_RESET+" - "+ANSI_BRIGHT_PURPLE);
            for (int j = 0; j < finalAdd[3].length; j++) {
                if (j == 3){
                    System.out.print(finalAdd[3][j]+ANSI_RESET);
                }
                else {
                    System.out.print(finalAdd[3][j]+".");
                }
            }
            System.out.println(ANSI_BRIGHT_RED+"\nHosts\t:\t"+finalAdd[4][0]+ANSI_RESET);
        }
        else {//Process for IPv6 - if it's possible & i will have will (most likely no)

        }
    }
}
