import java.util.*;
import java.util.function.*;


public class Main {

    public static void main(String[] args) {
        // Random variables
        String randomFrom = "..."; // Некоторая случайная строка. Можете выбрать ее самостоятельно.
        String randomTo = "...";  // Некоторая случайная строка. Можете выбрать ее самостоятельно.
        int randomSalary = 100;  // Некоторое случайное целое положительное число. Можете выбрать его самостоятельно.

// Создание списка из трех почтовых сообщений.
        MailMessage firstMessage = new MailMessage(
                "Robert Howard",
                "H.P. Lovecraft",
                "This \"The Shadow over Innsmouth\" story is real masterpiece, Howard!"
        );

        assert firstMessage.getFrom().equals("Robert Howard"): "Wrong firstMessage from address";
        assert firstMessage.getTo().equals("H.P. Lovecraft"): "Wrong firstMessage to address";
        assert firstMessage.getContent().endsWith("Howard!"): "Wrong firstMessage content ending";

        MailMessage secondMessage = new MailMessage(
                "Jonathan Nolan",
                "Christopher Nolan",
                "Брат, почему все так хвалят только тебя, когда практически все сценарии написал я. Так не честно!"
        );

        MailMessage thirdMessage = new MailMessage(
                "Stephen Hawking",
                "Christopher Nolan",
                "Я так и не понял Интерстеллар."
        );

        List<MailMessage> messages = Arrays.asList(
                firstMessage, secondMessage, thirdMessage
        );

// Создание почтового сервиса.
        MailService<String> mailService = new MailService<>();

// Обработка списка писем почтовым сервисом
        messages.stream().forEachOrdered(mailService);

// Получение и проверка словаря "почтового ящика",
//   где по получателю можно получить список сообщений, которые были ему отправлены
        Map<String, List<String>> mailBox = mailService.getMailBox();

        assert mailBox.get("H.P. Lovecraft").equals(
                Arrays.asList(
                        "This \"The Shadow over Innsmouth\" story is real masterpiece, Howard!"
                )
        ): "wrong mailService mailbox content (1)";

        assert mailBox.get("Christopher Nolan").equals(
                Arrays.asList(
                        "Брат, почему все так хвалят только тебя, когда практически все сценарии написал я. Так не честно!",
                        "Я так и не понял Интерстеллар."
                )
        ): "wrong mailService mailbox content (2)";

        assert mailBox.get(randomTo).equals(Collections.<String>emptyList()): "wrong mailService mailbox content (3)";


// Создание списка из трех зарплат.
        Salary salary1 = new Salary("Facebook", "Mark Zuckerberg", 1);
        Salary salary2 = new Salary("FC Barcelona", "Lionel Messi", Integer.MAX_VALUE);
        Salary salary3 = new Salary(randomFrom, randomTo, randomSalary);

// Создание почтового сервиса, обрабатывающего зарплаты.
        MailService<Integer> salaryService = new MailService<>();

// Обработка списка зарплат почтовым сервисом
        Arrays.asList(salary1, salary2, salary3).forEach(salaryService);

// Получение и проверка словаря "почтового ящика",
//   где по получателю можно получить список зарплат, которые были ему отправлены.
        Map<String, List<Integer>> salaries = salaryService.getMailBox();

        assert salaries.get(salary1.getTo()).equals(Arrays.asList(1)): "wrong salaries mailbox content (1)";
        assert salaries.get(salary2.getTo()).equals(Arrays.asList(Integer.MAX_VALUE)): "wrong salaries mailbox content (2)";
        assert salaries.get(randomTo).equals(Arrays.asList(randomSalary)): "wrong salaries mailbox content (3)";

    }


    /*
Интерфейс: сущность, которую можно отправить по почте.
У такой сущности можно получить от кого и кому направляется письмо.
Пришлось добавить getContent, чтобы можно было это в mailservice обррабатывать
*/
    public static interface Sendable<T> {
        String getFrom();
        String getTo();
        T getContent();
    }



    /*
Письмо, у которого есть текст, который можно получить с помощью метода `getContent`
*/
    public static class MailMessage implements Sendable<String> {

        private final String from;
        private final String to;
        private final String message;

        public MailMessage(String from, String to, String message) {
            this.from = from;
            this.to = to;
            this.message = message;
        }

        @Override
        public String getFrom() {
            return from;
        }

        @Override
        public String getTo() {
            return to;
        }

        public String getContent() {
            return message;
        }


    }

//тут с Salary вроде всё понятно
    public static class Salary implements Sendable<Integer>{

        private final String from;
        private final String to;
        private final Integer salary;

        public Salary(String from, String to, Integer salary) {

            this.from = from;
            this.to = to;
            this.salary = salary;
        }

        @Override
        public String getFrom() {
            return from;
        }

        @Override
        public String getTo() {
            return to;
        }

        @Override
        public Integer getContent() {
            return salary;
        }

        // implement here
    }
//долго парился с добавлением элементов. Надо понимать как работает HashMap и знать, что если ключ найден в мапе,
    //то он перезаписывает данные с новым значением
    public static class MailService<T> extends HashMap<String,List<T>> implements Consumer<Sendable<T>> {
        private Map<String,List<T>> mails = new HashMap<String, List<T>>(){
            @Override
            public List<T> get(Object key) {
                //возвращает значение если есть - и создаёт пустой linked list, если нет такого ключа
                    return super.getOrDefault(key,new LinkedList<T>());
            }
        };










        public Map<String, List<T>> getMailBox() {
            return mails;
        }


        //взято отсюда http://qaru.site/questions/1217948/java-adding-another-string-value-to-existing-hashmap-key-without-overwriting
        @Override
        public void accept(Sendable<T> tSendable) {
            List<T> inpList;
            //inpList.add(tSendable.getContent());
            //mails.put(tSendable.getTo(),inpList);


            if(mails.containsKey(tSendable.getTo())){
                // if the key has already been used,
                // we'll just grab the array list and add the value to it
                inpList = mails.get(tSendable.getTo());
                inpList.add(tSendable.getContent());
            } else {
                // if the key hasn't been used yet,
                // we'll create a new ArrayList<String> object, add the value
                // and put it in the array list with the new key
                inpList = new LinkedList<>();
                inpList.add(tSendable.getContent());
                mails.put(tSendable.getTo(), inpList);
            }
        }
    }
}
