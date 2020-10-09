package au.edu.sydney.cpa.erp.view;

import java.util.Scanner;

class ViewUtils {
    static int displayMenu(String header, String[] options, String prompt) {
        System.out.println("\n" + header);

        for (int i = 0; i < options.length; i++) {
            System.out.println((i+1) + ". " + options[i]);
        }

        while (true) {
            Integer response = getInt(prompt, true);

            int selection = response != null ? response : -1;

            if (selection > 0 && selection <= options.length) {
                return selection;
            } else {
                System.out.println("Invalid menu selection");
            }
        }
    }

    static String getString(String prompt, boolean allowBlank) {
        Scanner s = new Scanner(System.in);

        String response;
        do {
            System.out.println(prompt);
            response = s.nextLine();

            if (!allowBlank && "".equals(response)) {
                response = null;
                System.out.println("Blank entry is not allowed here.");
            }
        } while (null == response);

        return response;
    }

    static Integer getInt(String prompt, boolean allowBlank) {

        int response;
        do {
            String str = getString(prompt, allowBlank);
            if ("".equals(str)) {
                return null;
            }
            try {
                response = Integer.parseInt(str);
                return response;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input - number required");
            }

        } while (true);
    }

    static Boolean getBoolean(String prompt, boolean allowBlank) {
        prompt = prompt + "(y/n) ";
        Boolean response;
        do {
            String str = getString(prompt, allowBlank);
            if ("".equals(str)) {
                return null;
            }

            if ("y".equals(str.toLowerCase())) {
                return true;
            }

            if ("n".equals((str.toLowerCase()))) {
                return false;
            }

            System.out.println("Invalid input - must be y or n");

        } while (true);
    }
}
