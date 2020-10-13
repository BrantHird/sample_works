import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;


public class MyClassifier {

    static String trainingFile;
    static String testingFile;
    static String type;

    static ArrayList<Float> sumOfAttributesY = new ArrayList<Float>();
    static ArrayList<Float> meanOfAttributesY = new ArrayList<Float>();
    static ArrayList<Float> sdOfAttributesY = new ArrayList<Float>();

    static ArrayList<Float> sumOfAttributesN = new ArrayList<Float>();
    static ArrayList<Float> meanOfAttributesN = new ArrayList<Float>();
    static ArrayList<Float> sdOfAttributesN = new ArrayList<Float>();


    public static void main(String args[]){

        if(args.length != 3){
            System.out.println("Incorrect Number of Arguments");
        }
        else{
            trainingFile = args[0];
            testingFile = args[1];
            type = args[2];


            if(type.equals("NB")){
                float yes = 0 ;
                float no = 0 ;
                float counter = 0;
                try {

                    File training = new File(trainingFile);
                    Scanner trainReader = new Scanner(training);
                    while (trainReader.hasNextLine()) {
                        String data = trainReader.nextLine();

                        String[] arrOfStr =  data.split(",");



                        if(arrOfStr[arrOfStr.length-1].equals("yes")){
                            yes++;

                            if(sumOfAttributesY.size() == 0 ){
                                for(int i = 0 ; i < arrOfStr.length-1;i++){
                                    sumOfAttributesY.add(Float.parseFloat(arrOfStr[i]));
                                }
                            }
                            else{
                                int count = 0 ;
                                for(int i = 0 ; i < arrOfStr.length-1;i++){
                                    float sum = sumOfAttributesY.get(count) + Float.parseFloat(arrOfStr[i]);
                                    sumOfAttributesY.set(count,sum);
                                    count++;
                                }
                            }


                        }
                        else{
                            no++;
                            if(sumOfAttributesN.size() == 0 ){
                                for(int i = 0 ; i < arrOfStr.length-1;i++){
                                    sumOfAttributesN.add(Float.parseFloat(arrOfStr[i]));
                                }

                            }
                            else{
                                int count = 0 ;
                                for(int i = 0 ; i < arrOfStr.length-1;i++){
                                    float sum = sumOfAttributesN.get(count) + Float.parseFloat(arrOfStr[i]);
                                    sumOfAttributesN.set(count,sum);
                                    count++;
                                }
                            }

                        }


                        counter++;
                    }
                    trainReader.close();

                } catch (FileNotFoundException e) {
                    System.out.println("An error occurred.");
                    e.printStackTrace();
                }


                for(float a : sumOfAttributesY){
                    meanOfAttributesY.add(a/yes);


                }


                for(float a : sumOfAttributesN){
                    meanOfAttributesN.add(a/no);

                }


                try {

                    File training = new File(trainingFile);
                    Scanner trainReader = new Scanner(training);
                    while (trainReader.hasNextLine()) {
                        String data = trainReader.nextLine();
                        String[] arrOfStr =  data.split(",");

                        if(arrOfStr[arrOfStr.length-1].equals("yes")){
                            if(sdOfAttributesY.size() == 0 ){
                                for(int i = 0 ; i<arrOfStr.length-1;i++){
                                    sdOfAttributesY.add((float) Math.pow(Float.parseFloat(arrOfStr[i]) - meanOfAttributesY.get(i),2));
                                }
                            }
                            else{
                                for(int i = 0 ; i<arrOfStr.length-1;i++){
                                    float sd = (float) (sdOfAttributesY.get(i) +  Math.pow(Float.parseFloat(arrOfStr[i]) - meanOfAttributesY.get(i),2));
                                    sdOfAttributesY.set(i,sd);
                                }
                            }
                        }
                        else{
                            if(sdOfAttributesN.size() == 0){
                                for(int i = 0 ; i<arrOfStr.length-1;i++){
                                    sdOfAttributesN.add((float) Math.pow(Float.parseFloat(arrOfStr[i]) - meanOfAttributesN.get(i),2));
                                }
                            }
                            else{
                                for(int i = 0 ; i<arrOfStr.length-1;i++){
                                    float sd = (float) (sdOfAttributesN.get(i) +  Math.pow(Float.parseFloat(arrOfStr[i]) - meanOfAttributesN.get(i),2));
                                    sdOfAttributesN.set(i,sd);
                                }
                            }
                        }



                    }
                    trainReader.close();

                } catch (FileNotFoundException e) {
                    System.out.println("An error occurred.");
                    e.printStackTrace();
                }



                int count = 0 ;

                for(float a : sdOfAttributesY){

                    float res = a/(yes-1);

                    float sd = (float) Math.sqrt(res);



                    sdOfAttributesY.set(count,sd);



                    count++;
                }

                int count1 = 0 ;

                for(float a : sdOfAttributesN){

                    float sd = (float) Math.sqrt(a/(no-1));



                    sdOfAttributesN.set(count1,sd);

                    count1++;
                }


                try {

                    File testing = new File(testingFile);
                    Scanner testReader = new Scanner(testing);                   // this where i do the logic of the testing
                    while (testReader.hasNextLine()) {
                        String data = testReader.nextLine();
                        String[] arrOfStr =  data.split(",");

                        float finalProbY = 1;
                        float finalProbN = 1;

                        for(int i = 0 ; i <arrOfStr.length ; i++){
                            float x = Float.parseFloat(arrOfStr[i]);

                            float probabilityY = (float) ((1/((sdOfAttributesY.get(i))*Math.sqrt(2*Math.PI)))*Math.exp(-Math.pow(x-meanOfAttributesY.get(i),2)/(2*Math.pow(sdOfAttributesY.get(i),2))));
                            float probabilityN = (float) ((1/((sdOfAttributesN.get(i))*Math.sqrt(2*Math.PI)))*Math.exp(-Math.pow(x-meanOfAttributesN.get(i),2)/(2*Math.pow(sdOfAttributesN.get(i),2))));


                            finalProbN = finalProbN * probabilityN;
                            finalProbY = finalProbY * probabilityY;


                        }



                        float noMult = (float)(no/counter);
                        float yesMult = (float)(yes/counter);




                        finalProbN = finalProbN * noMult;
                        finalProbY = finalProbY * yesMult;




                        if(finalProbN>finalProbY){
                            System.out.println("No");
                        }
                        else{
                            System.out.println("Yes");
                        }

                    }
                    testReader.close();

                } catch (FileNotFoundException e) {
                    System.out.println("An error occurred.");
                    e.printStackTrace();
                }






            }
            else{
                char[] ch = type.toCharArray();

                int k = Character.getNumericValue(ch[0]);

                try {

                    File testing = new File(testingFile);
                    Scanner testReader = new Scanner(testing);                   // this where i do the logic of the testing

                    int te = 0 ;

                    while (testReader.hasNextLine()) {
                        String data = testReader.nextLine();
                        String[] arrOfStr =  data.split(",");

                        HashMap<Float,String> distance = new HashMap<Float,String>();


                        try {

                            File training = new File(trainingFile);
                            Scanner trainReader = new Scanner(training);

                            while (trainReader.hasNextLine()) {
                                String da = trainReader.nextLine();

                                String[] arr =  da.split(",");

                                float d = 0 ;
                                for(int i = 0 ; i<arrOfStr.length;i++){

                                     d = (float) (d + Math.pow(Float.parseFloat(arrOfStr[i]) - Float.parseFloat(arr[i]),2));

                                }




                                d= (float) Math.sqrt(d);

                                distance.put(d,arr[arr.length-1]);

                            }
                            trainReader.close();

                        } catch (FileNotFoundException e) {
                            System.out.println("An error occurred.");
                            e.printStackTrace();
                        }



                        Map<Float, String> sort = new TreeMap<Float, String>(distance);

                        Set sorted = sort.entrySet();

                        Iterator iterate = sorted.iterator();

                        int i = 0 ;
                        int yep = 0 ;
                        int nup = 0 ;



                        while(i < k) {
                            Map.Entry value = (Map.Entry)iterate.next();




                            if(value.getValue().equals("yes")){
                                yep++;
                            }
                            else{
                                nup++;
                            }

                            i++;
                        }

                        if(nup>yep){
                            System.out.println("No");
                        }
                        else{
                            System.out.println("Yes");
                        }


                    }
                    testReader.close();

                } catch (FileNotFoundException e) {
                    System.out.println("An error occurred.");
                    e.printStackTrace();
                }





            }
        }
    }

}
