import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.*;
import java.util.*;

/**
 * Class that is used for checking all solutions inside the directory.
 * There is a file name.txt, in which names and surnames (NameSurname) of all students can be found.
 * Input files are just input files.
 * Output files can contain only these messages: "Invalid input." or "N", where N is the number of badness points for
 * the most optimal solution currently found (it can be better).
 */
public class SolutionChecker {
    private static final int COURSES = 0;
    private static final int PROFESSORS = 1;
    private static final int TAS = 2;
    public static final int STUDENTS = 3;

    private static final int ZERO_COURSES = 0;
    private static final int LACKING_CLASS = 1;
    private static final int UNTRAINED_CLASS = 2;
    private static final int TWO_CLASSES = 3;

    private static final int INVALID_INPUT = -1;
    private static final int OUTPUT_ERROR = -2;
    private static final int MAX_CODE_GRADE = 16;

    private static final int MAX_FILES = 50;
    private static final String output = "output%d.txt"; // correct solution files name
    private static final String input = "input%d.txt"; // input files name
    private static final String GRADES_SHEET = "Grades";
    private String feedbackDir = "feedback/";
    private ArrayList<String> names;
    private ArrayList<Double> grades;
    private String namesFile;
    private String gradesFile;
    private boolean createTable;

    /**
     * Contains of best solutions for i-th input.
     * -1 if "Invalid input."
     */
    private ArrayList<Integer> bestSolutions; // rewritable in case when some student got better solution

    /**
     * All students solutions.
     */
    private ArrayList<ArrayList<JudgeFeedback>> studentsSolutions;

    public SolutionChecker(String namesFile, String gradesFile) throws IOException {
        names = scanAllNames(namesFile);
        this.namesFile = namesFile;
        this.gradesFile = gradesFile;
    }

    public void check(boolean createTable) throws IOException {
        if (this.createTable = createTable)
            createTable();
        getBestSolutions();

        for (int i = 0; i < bestSolutions.size(); i++) {
            checkOutputN(i + 1);
        }

        evaluateGrades();

        if (createTable) {
            saveGrades(gradesFile);
            saveFeedbacks();
        }

    }

    /**
     * Check all outputs for input_number.txt.
     * @param number 1 - N. Number of the input file.
     */
    private void checkOutputN(int number) {
        LinkedHashMap<String, Course> courses = new LinkedHashMap<>();
        LinkedHashMap<String, Professor> profs = new LinkedHashMap<>();
        LinkedHashMap<String, TA> tas = new LinkedHashMap<>();
        LinkedHashMap<String, Student> studs = new LinkedHashMap<>();
        readInput(number, courses, profs, tas, studs);

        for (String name : names) {
            studentsSolutions.get(number - 1).add(getGrade(number, String.format("%sOutput%d.txt", name, number), courses, profs, tas, studs));
        }
    }

    private void evaluateGrades() {

        for (int i = 0; i < names.size(); i++) {
            double grade = 0;
            for (int j = 0; j < studentsSolutions.size(); j++) {
                if (studentsSolutions.get(j).get(i).getStatus() != OUTPUT_ERROR)
                    grade +=  errorFunction(studentsSolutions.get(j).get(i).getStatus() - bestSolutions.get(j));
            }
            grade /= studentsSolutions.size();

            grades.add(grade * MAX_CODE_GRADE);
        }
    }

    /**
     *
     * @param n
     * @param courses
     * @param profs
     * @param tas
     * @param studs
     */
    private void readInput(int n, LinkedHashMap<String, Course> courses, LinkedHashMap<String, Professor> profs, LinkedHashMap<String, TA> tas, LinkedHashMap<String, Student> studs) {
        try {
            ArrayList<String> states = new ArrayList<>(Arrays.asList("P", "T", "S"));
            if (bestSolutions.get(n - 1) == INVALID_INPUT) return;
            FileReader fileReader = new FileReader(String.format(input, n));
            Scanner scanner = new Scanner(fileReader);
            int state = COURSES;

            String line;
            while (scanner.hasNextLine()) {
                line = scanner.nextLine();
                if (states.contains(line)) {
                    state++;
                    continue;
                }
                String info[] = line.split(" ");
                if (state == COURSES) {

                    Course course = new Course(info[0], courses.size(), Integer.parseInt(info[1]), Integer.parseInt(info[2]));
                    courses.put(course.getName(), course);

                } else if (state == PROFESSORS) {

                    Professor professor = new Professor(info[0] + " " + info[1], profs.size());
                    for (String course: Arrays.copyOfRange(info, 2, info.length)) {
                        professor.addCourse(course);
                    }
                    profs.put(professor.getName(), professor);

                } else if (state == TAS) {

                    TA ta = new TA(info[0] + " " + info[1], tas.size());
                    for (String course: Arrays.copyOfRange(info, 2, info.length)) {
                        ta.addCourse(course);
                    }
                    tas.put(ta.getName(), ta);

                } else {
                    Student student = new Student(info[0] + " " + info[1] + " " + info[2], studs.size());
                    for (String course: Arrays.copyOfRange(info, 3, info.length)) {
                        student.addCourse(course);
                    }
                    studs.put(student.getName(), student);

                }
            }

            scanner.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    Course findCourseByName(ArrayList<Course> courseArrayList, String name) {
        for (Course c: courseArrayList) {
            if (c.getName().equals(name)) return c;
        }

        return null;
    }

    @SuppressWarnings({"unchecked"})
    private JudgeFeedback getGrade(int n, String file, LinkedHashMap<String, Course> courses, LinkedHashMap<String, Professor> profs, LinkedHashMap<String, TA> tas, LinkedHashMap<String, Student> studs) {
        ArrayList<ArrayList<String>> segments;
        JudgeFeedback judgeFeedback = new JudgeFeedback();
        try {
            segments = getInputSegments(new FileReader(file));
        } catch (FileNotFoundException e) {
            return judgeFeedback.setVerdict(OUTPUT_ERROR, n, "Output file is not found.");
        }

        if (bestSolutions.get(n - 1) == INVALID_INPUT) {
            return segments.size() == 1
                    && segments.get(0).size() <= 2
                    && segments.get(0).size() >= 1
                    && segments.get(0).get(0).matches("Invalid input.")
                        ? judgeFeedback.setVerdict(INVALID_INPUT, n, "SUCCESS")
                        : judgeFeedback.setVerdict(OUTPUT_ERROR, n, "Invalid input expected but something else is found.");
        }


        boolean[] cRun = new boolean[courses.size()]; // cRun[i] - will i-th course be runned
        int[] pBusy = new int[profs.size()]; // pBusy[i] - how prof i is busy.
        int[] tBusy = new int[tas.size()]; // tBusy[i] - how ta i is busy.
        int[] untrainedCourses = new int[profs.size()]; // pBusy[i] - untrained course (if exists).
        ArrayList<HashSet<String>> studCourses = new ArrayList<>(studs.size());

        Iterator<Student> studIterator = studs.values().iterator();
        for (int i = 0; i < studs.size(); i++) {
            studCourses.add((HashSet<String>) studIterator.next().getCourses().clone());
        }


        int currentCourse = -1;
        /*
            Check courses.
         */
        for (int i = 0; i < segments.size() - 1; i++) {
            ArrayList<String> lines = segments.get(i);
            Course course = null;
            for (int j = 0; j < lines.size(); j++) {

                if (j == 0){ // should be a name of a course

                    if (courses.get(lines.get(j)) == null || (course = courses.get(lines.get(j))).getId() <= currentCourse) {
                        if (course == null) {
                            return judgeFeedback.setVerdict(OUTPUT_ERROR, n, String.format("Unknown course \"%s\" is found.", lines.get(j)));
                        }
                        return judgeFeedback.setVerdict(OUTPUT_ERROR, n, "Arrangement of courses is invalid.");
                    }

                    currentCourse = course.getId();
                    cRun[currentCourse] = true;
                } else if (j == 1) { // should be a name of a professor
                    Professor prof = profs.get(lines.get(j));
                    if (prof == null)
                        return judgeFeedback.setVerdict(OUTPUT_ERROR, n, String.format("Unknown professor \"%s\" is found.", lines.get(j)));
                    switch (pBusy[prof.getId()]) {
                        case ZERO_COURSES:

                            if (prof.getCourses().contains(lines.get(0))) {
                                pBusy[prof.getId()] = LACKING_CLASS;
                            } else {
                                pBusy[prof.getId()] = UNTRAINED_CLASS;
                                untrainedCourses[prof.getId()] = currentCourse;
                            }

                            break;
                        case LACKING_CLASS:

                            if (prof.getCourses().contains(lines.get(0))) {
                                pBusy[prof.getId()] = TWO_CLASSES;
                            } else {
                                return judgeFeedback.setVerdict(OUTPUT_ERROR, n, String.format("Professor \"%s\" is assigned to more than one course, one of which they is not trained for.", prof.getName()));
                            }

                            break;
                        case UNTRAINED_CLASS:

                            return judgeFeedback.setVerdict(OUTPUT_ERROR, n, String.format("Professor \"%s\" is assigned to more than one course, one of which they is not trained for.", prof.getName())); // one professor assigned to untrained course + some course
                        case TWO_CLASSES:
                            return judgeFeedback.setVerdict(OUTPUT_ERROR, n, String.format("Professor \"%s\" is assigned to more than two courses.", prof.getName())); // one professor is assigned to more that 2 classes
                    }


                } else if (j < 2 + course.getTAs()) {
                    TA ta = tas.get(lines.get(j));
                    if (ta == null || !ta.getCourses().contains(lines.get(0)) || tBusy[ta.getId()] == 4) {
                        if (ta == null) return judgeFeedback.setVerdict(OUTPUT_ERROR, n, String.format("Unknown TA \"%s\" is found.", lines.get(j)));
                        return judgeFeedback.setVerdict(OUTPUT_ERROR, n, String.format("TA \"%s\" is assigned to more than four labs.", lines.get(j)));
                    }

                    tBusy[ta.getId()]++;

                } else if (j >= 2 + course.getTAs() && j < 2 + course.getTAs() + course.getAllowedStudents()) {
                    Student stud = studs.get(lines.get(j));

                    if (stud == null || /*!stud.getCourses().contains(lines.get(0)) || */!studCourses.get(stud.getId()).remove(lines.get(0))) {
                        if (stud == null)
                            return judgeFeedback.setVerdict(OUTPUT_ERROR, n, String.format("Unknown student \"%s\" is found.", lines.get(j)));

                        return judgeFeedback.setVerdict(OUTPUT_ERROR, n, String.format("Student \"%s\" is assigned to course \"%s\" they did not ask for.", lines.get(j), lines.get(0)));
                    }

                } else {
                    return judgeFeedback.setVerdict(OUTPUT_ERROR, n, String.format("The number of students assigned to course \"%s\" is more than what the course can handle.", lines.get(0)));
                }
            }
        }


        ArrayList<String> badness20 = new ArrayList<>();
        ArrayList<String> badness10 = new ArrayList<>();
        ArrayList<String> badness5 = new ArrayList<>();
        ArrayList<String> badness2 = new ArrayList<>();
        ArrayList<String> badness1 = new ArrayList<>();
        int badnessPoints = 0;

        ArrayList<Course> coursesList = new ArrayList<>(courses.values());
        for (int i = 0; i < cRun.length; i++) {
            boolean aCRun = cRun[i];
            if (!aCRun) {
                badness20.add(String.format("%s cannot be run.", coursesList.get(i).getName()));
                badnessPoints += 20;
            }
        }


        ArrayList<Professor> profList = new ArrayList<>(profs.values());
        for (int i = 0; i < pBusy.length; i++) {
            if (pBusy[i] == ZERO_COURSES) {
                badness10.add(String.format("%s is unassigned.", profList.get(i).getName()));
                badnessPoints += 10;
            }
            else if (pBusy[i] == LACKING_CLASS) {
                badness5.add(String.format("%s is lacking class.", profList.get(i).getName()));
                badnessPoints += 5;
            }
            else if (pBusy[i] == UNTRAINED_CLASS) {
                badness5.add(String.format("%s is not trained for %s.", profList.get(i).getName(), coursesList.get(untrainedCourses[i]).getName()));
                badnessPoints += 5;
            }
        }

        ArrayList<TA> taList = new ArrayList<>(tas.values());
        for (int i = 0; i < tBusy.length; i++) {
            if (tBusy[i] < 4) {
                badness2.add(String.format("%s is lacking %d lab(s).", taList.get(i).getName(), 4 - tBusy[i]));
                badnessPoints += (4 - tBusy[i]) * 2;
            }
        }

        ArrayList<Student> studList = new ArrayList<>(studs.values());
        for (int i = 0; i < studList.size(); i++) {
            String[] studentCourses =  studCourses.get(i).toArray(new String[0]);
            for (String course: studentCourses) {
                String[] name = Arrays.copyOfRange(studList.get(i).getName().split(" "), 0, 2);
                badness1.add(String.format("%s is lacking %s.", String.join(" ", name), course));
                badnessPoints++;
            }
        }

        ArrayList<String> errLines = segments.get(segments.size() - 1);
        MultiSet<String> studentBadness20 = new HashMultiSet<>();
        MultiSet<String> studentBadness10 = new HashMultiSet<>();
        MultiSet<String> studentBadness5 = new HashMultiSet<>();
        MultiSet<String> studentBadness2 = new HashMultiSet<>();
        MultiSet<String> studentBadness1 = new HashMultiSet<>();
        for (int i = 0; i < errLines.size(); i++) {
            if (i < badness20.size())
                studentBadness20.add(errLines.get(i));
            else if (i < badness10.size() + badness20.size())
                studentBadness10.add(errLines.get(i));
            else if (i < badness5.size() + badness10.size() + badness20.size())
                studentBadness5.add(errLines.get(i));
            else if (i < badness2.size() + badness5.size() + badness10.size() + badness20.size())
                studentBadness2.add(errLines.get(i));
            else if (i < badness1.size() + badness2.size() + badness5.size() + badness10.size() + badness20.size())
                studentBadness1.add(errLines.get(i));
            else if (i == badness1.size() + badness2.size() + badness5.size() + badness10.size() + badness20.size()) {
                if (!errLines.get(i).matches(String.format("Total score is %d.", badnessPoints)))
                    return judgeFeedback.setVerdict(OUTPUT_ERROR, n, String.format("Badness points are calculated incorrectly. Expected %d. Got \"%s\"", badnessPoints, errLines.get(i))); // badness points are calculated incorrectly
            } else if (!errLines.get(i).matches(""))
                return judgeFeedback.setVerdict(OUTPUT_ERROR, n, "Excessive number of lines is found.");
        }


        for (String err20 : badness20) {
            studentBadness20.remove(err20);
        }
        if (!studentBadness20.isEmpty())
            return judgeFeedback.setVerdict(OUTPUT_ERROR, n, "20-badness-points section is not correct.");


        for (String err10 : badness10) {
            studentBadness10.remove(err10);
        }
        if (!studentBadness10.isEmpty())
            return judgeFeedback.setVerdict(OUTPUT_ERROR, n, "10-badness-points section is not correct.");


        for (String err5 : badness5) {
            studentBadness5.remove(err5);
        }
        if (!studentBadness5.isEmpty())
            return judgeFeedback.setVerdict(OUTPUT_ERROR, n, "5-badness-points section is not correct.");


        for (String err2 : badness2) {
            studentBadness2.remove(err2);
        }
        if (!studentBadness2.isEmpty())
            return judgeFeedback.setVerdict(OUTPUT_ERROR, n, "2-badness-points section is not correct.");


        for (String err1 : badness1) {
            studentBadness1.remove(err1);
        }
        if (!studentBadness1.isEmpty())
            return judgeFeedback.setVerdict(OUTPUT_ERROR, n, "1-badness-points section is not correct.");

        if (badnessPoints < bestSolutions.get(n - 1))
            bestSolutions.set(n - 1, badnessPoints);

        return judgeFeedback.setVerdict(badnessPoints, n, "");
    }

    private ArrayList<ArrayList<String>> getInputSegments(FileReader fileReader) {
        ArrayList<ArrayList<String>> segments = new ArrayList<>();
        segments.add(new ArrayList<>());
        Scanner scanner = new Scanner(fileReader);

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (line.equals("")) {
                segments.add(new ArrayList<>());
            } else {
                segments.get(segments.size() - 1).add(line);
            }
        }

        scanner.close();
        return segments;
    }

    /**
     * @param namesFile name of the file where all names are stored
     * @return list with all names
     * @throws IOException if there is any problem with opening/closing files
     */
    private ArrayList<String> scanAllNames(String namesFile) throws IOException {
        ArrayList<String> names = new ArrayList<>();

        FileReader fileReader = new FileReader(namesFile);
        Scanner scanner = new Scanner(fileReader);

        while (scanner.hasNextLine()) {
            names.add(scanner.nextLine());
        }

        fileReader.close();
        return names;
    }

    private void createTable() throws IOException {
        File file = new File(feedbackDir + gradesFile);

        File parentFile = file.getParentFile();
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }

        Workbook book = new HSSFWorkbook();
        Sheet sheet = book.createSheet(GRADES_SHEET);

        Row row = sheet.createRow(0);

        row.createCell(0).setCellValue("Number");
        row.createCell(1).setCellValue("Names");
        row.createCell(2).setCellValue("Emails");
        row.createCell(3).setCellValue("Grade");
        row.createCell(4).setCellValue("Feedback");



        for (int i = 0; i < names.size(); i++) {
            Row r = sheet.createRow(i + 1);
            r.createCell(0).setCellValue(i + 1);
            r.createCell(1).setCellValue(names.get(i));
            String email = "";
            try {
                FileReader fileReader1 = new FileReader(names.get(i) + "Email.txt");
                email = new Scanner(fileReader1).nextLine();
            } catch (FileNotFoundException ignored) {}

            r.createCell(2).setCellValue(email);
        }


        book.write(new FileOutputStream(feedbackDir + gradesFile));
        book.close();
    }

    private void saveGrades(String gradesFile) throws IOException {
        FileInputStream inputStream = new FileInputStream(new File(feedbackDir + gradesFile));
        Workbook book = new HSSFWorkbook(inputStream);
        Sheet sheet = book.getSheet(GRADES_SHEET);

        for (int i = 0; i < names.size(); i++) {
            sheet.getRow(i + 1).createCell(3).setCellValue(grades.get(i) );
        }

        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
        sheet.autoSizeColumn(2);
        sheet.autoSizeColumn(3);
        sheet.autoSizeColumn(4);

        inputStream.close();
        FileOutputStream outputStream = new FileOutputStream(feedbackDir + gradesFile);
        book.write(outputStream);
        book.close();
        outputStream.close();

    }

    private void saveFeedbacks() throws IOException {

        for (int i = 0; i < names.size(); i++) {
            FileWriter fileWriter = new FileWriter(feedbackDir + names.get(i) + "Feedback.txt");
            for (int j = 0; j < bestSolutions.size(); j++) {
                JudgeFeedback judgeFeedback = studentsSolutions.get(j).get(i);
                fileWriter.write(judgeFeedback.getVerdict(bestSolutions.get(j)));
            }

            fileWriter.close();
        }

    }

    /**
     * Read all outputs file.
     * We assume that the number of input files are the same as the number of output files.
     */
    private void getBestSolutions() {
        bestSolutions = new ArrayList<>(MAX_FILES);
        studentsSolutions = new ArrayList<>(MAX_FILES);
        grades = new ArrayList<>();

        for (int i = 0; i < MAX_FILES; i++) {
            try {
                FileReader fileReader = new FileReader(String.format(output, i + 1));
                String line = new Scanner(fileReader).nextLine();
                //grades.add(new TaskGradesList(names.size()));
                if (line.matches("Invalid input."))
                    bestSolutions.add(INVALID_INPUT);
                else if (line.matches("[0-9]+")) {
                    bestSolutions.add(Integer.parseInt(line));
                }
                studentsSolutions.add(new ArrayList<>());

                fileReader.close();
            } catch (FileNotFoundException e) {
                break;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static double errorFunction(int x) {
        final double OLEZHA = 0.95;
        return Math.pow(OLEZHA, x);
    }
}
