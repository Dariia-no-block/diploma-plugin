Для початку потрібно зайти в гугл аккаунт і діскорд. Дані для входу надішлю в ТГ.

Запуск
1. Відкриваємо Docker
2. Заходимо в IDEA, шукаємо в дереві проекту файл docker-compose.yaml, запускаємо його.
3. Запускаємо сам джава проект з класу DiplomaPluginApplication.

Використання: 
1. Реєстрація викладача. Заходимо в код пакет command -> RegisterCommand.java -> рядок 47 -> міняємо STUDENT на TEACHER.
2. Заходимо в діскорд і ввордимо команду /register, заповнюємо необхідні поля. Діскорд видасть помилку, бо поки відсутні створені групи в базі даних. Але таким чином зареєструється адмін. 
3. Змінюємо код назад на STUDENT. 
4. З аккаунту адміністратора створюємо кілька груп команою /create_group. Тут потрібно буде перейти в IDEA при першому запуску. В консолі буде повідомлення про те, що небхідно авторизуватись в гугл аккаунт для даного застосунку. Тому натискаємо на посилання в консолі і проходимо стандартну аавторизацію і надаємо всі дозволи.
5. Реєструємо новий акк Discord, потім реєструємось в плагіні командою /register. Тут вже буде достпуний вибір груп. Інколи діскорд теж може повертати помилку через затримки. Діскорд дає 3 секунди на повернення відповіді. Під час реєстрації студента для нього створюється окрема директорія на гугл диску, також його ім'я і прізвище додаються в гугл таблицю, це інколи може тривати більше 3-х секунд. 
6. Тут можна зайти на гугл диск, побачити структуру директорій, також можна зайти і подивитись таблицю. 
7. Створюємо з аккаунту викладача завдання. Для цього вводимо команду /create_task, спочатку прикріпляємо файл, вводимо назву файлу (ВАЖЛИВО!) щоб назва файлу не містила розділових знаків, пробілів, тощо (наприклад, Лабораторна1). Відправляємо команду, плагін пропонує обрати групу, якій при значити завдання. Обираємо групу, на цьому етапі завдання призначене. 
8. Ідемо в аккаунт студента, пишемо команду /menu і обираємо "Мої завдання". Тут буде видно призначене з минулого пункту завдання, його можна або подивитись, або здати. 
9. Здаємо завдання - для цього потрібно спочатку натиснути кнопку submit, після чого ввести команду /send, прикріпити файл, ввести назву файлу і відправити. Файл потрапить на гугл диск в папку студента.
10. Створення тестування. Для цього йдемо в аккаунт адміна і вводимо команду /creeate_test Вводимо назву тесту. Потім його потрібно наповнити питаннями. Для цього вводимо команду /add_question і обираємо потрібний тест (їх може бути декілька). Далі обраний тест зберігається в пам'яті. Циклічно вводимо команду /submit_question 
куди вводимо дані відповідно підказкам в діскорд, поки не занесемо всі питання до тесту. 
11. Призначення тестування. Для цього вводимо команду /start_test -> обираємо групу -> вводимо команду /set_duration і вводимо тривалість тесту в хвилинах. 
12. Переходимо в аккаунт студента. /menu -> Pass a test -> проходимо тест. Отримуємо результат, результат також прийде у вигляді повідомлення викладачу (ВАЖЛИВО!!!) Статус викладача обов'язково має бути "В мережі", інакше повідомлення не будуть приходити.
13. В таблиці ставимо кілька оціновк в рядку студента. Потім ідемо в студента і переглядаємо успішність /menu -> My success rate -> отримуємо суму всіх балів. 

+ додатково надішлю Postman колекцію для перегляду даних в БД
