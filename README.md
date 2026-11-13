# Campus Skill Swap  (AOOP Project - Java Swing + JDBC + MySQL)

Students offer skills they know, find skills they want to learn, and send **swap requests**
to each other. Built for 2nd year B.Tech CSE (Data Science) - Advanced Object Oriented Programming.

## 1. How to run (5 steps)

1. Install **JDK 8 or higher** and **MySQL**.
2. Open MySQL Workbench and run `sql/schema.sql` (creates the database, tables and sample data).
3. Copy the MySQL driver jar (`mysql-connector-j-x.x.x.jar`) into the `lib/` folder.
4. Open `db.properties` and put your MySQL username and password.
5. Double click `compile.bat` then `run.bat`  (Linux/Mac: `./compile.sh` then `./run.sh`).

Run all commands from the project root folder (the one that contains `src`, `lib`, `db.properties`).

**IDE (IntelliJ / Eclipse / NetBeans / VS Code):** create a Java project, set `src` as the source folder,
add the jar from `lib/` to the classpath, and run `campusskillswap.Main`. Set the working directory to the
project root so that `db.properties` is found.

**Demo logins** (sample students, password `pass123`): rahul@campus.edu, priya@campus.edu, arjun@campus.edu
Or click **Register** and create your own account.

## 2. Project structure

```
CampusSkillSwap/
|-- sql/schema.sql              database script (5 tables + sample data)
|-- db.properties               MySQL url / user / password
|-- lib/                        put mysql-connector-j jar here
|-- compile.bat, run.bat        (and .sh versions)
`-- src/campusskillswap/
    |-- Main.java               starts the app
    |-- interfaces/   (5)       Validatable, Displayable, CrudDAO<T>, Refreshable, NotificationListener
    |-- exceptions/   (3)       InvalidInputException, AuthenticationException, DuplicateEntryException
    |-- model/        (6)       Person (abstract) -> Student, Skill, SkillOffer, SwapRequest, StudentPreference
    |-- dao/          (6)       DBConnection, StudentDAO, SkillDAO, OfferDAO, SwapRequestDAO, PreferenceDAO
    |-- util/         (6)       Validator, PasswordUtil, Session, ProfileCalculator, UIHelper
    |-- threads/      (2)       ClockThread (Runnable), NotificationThread (extends Thread)
    `-- ui/           (10)      WelcomeFrame, LoginDialog, RegisterDialog, DashboardFrame, OverviewPanel,
                                ExploreSkillsPanel, PreferencesPanel, HelpPanel,
                                OfferSkillDialog, SwapRequestsDialog, ProfileDialog
```

Layers (simple 3-layer idea): **ui** (what the user sees) -> **dao** (SQL / JDBC) -> **MySQL**. `model` classes carry data between them.

## 3. Screens

| Screen | Class | What it does |
|---|---|---|
| First page | WelcomeFrame | Explore Skills (without login) / Login / Register |
| Login | LoginDialog | email + password, show password, remember email |
| Register | RegisterDialog | full form with validation |
| Dashboard | DashboardFrame | menu bar, 4 tabs, live clock, pending-request counter, logout |
| Overview tab | OverviewPanel | Offer a Skill, Find a Skill, Swap Requests, My Profile, profile completion bar |
| Explore Skills tab | ExploreSkillsPanel | category filter, search, sortable table, **Request Skill** |
| My Preferences tab | PreferencesPanel | learning mode, hours per week, interests, availability |
| Help tab | HelpPanel | topic tree + help text |
| Offer a Skill | OfferSkillDialog | add / remove the skills I teach |
| Swap Requests | SwapRequestsDialog | accept / reject received requests, cancel sent requests |
| My Profile | ProfileDialog | edit details, photo, change password |

## 4. Swap request flow (main feature)

1. Priya opens **Explore Skills**, selects "Java Programming" offered by Rahul, clicks **Request Skill**, types a message.
   -> `SwapRequestDAO.insert()` saves a row with status `PENDING`.
2. Rahul's dashboard status bar shows **Pending requests: 1** (`NotificationThread` checks the database every 8 seconds).
3. Rahul opens **Swap Requests -> Requests I received**, selects it and clicks **Accept** or **Reject**.
   -> `SwapRequestDAO.updateStatus()` changes the status. On Accept, Priya's contact details are shown.
4. Priya sees the new status (green / red) in **Requests I sent**. After Accept she can click **View Contact**.
   Pending requests can be cancelled by the sender.

## 4a. Database tables

`students` - `skills` (catalog) - `student_skills` (who offers what) - `preferences` - `swap_requests`

```
students 1---* student_skills *---1 skills
students 1---1 preferences
students 1---* swap_requests *---1 student_skills
```

## 5. Syllabus / review checklist - where to show each concept

**Review 2 - packages, interfaces, library, exceptions**

| Concept | Where |
|---|---|
| Packages | 7 packages listed above |
| Interface (own) | `interfaces/` - all 5 files |
| Generic interface | `CrudDAO<T>` implemented by StudentDAO, SkillDAO, OfferDAO, SwapRequestDAO |
| Default method in interface | `Displayable.getShortText()` |
| Built-in interfaces | `Runnable` (ClockThread), `Comparable` (Skill), `ActionListener` (Welcome/Login/Register), `ItemListener`, `ChangeListener`, `ListSelectionListener`, `TreeSelectionListener`, `DocumentListener` (UIHelper.onTextChange) |
| Abstract class + inheritance | `Person` -> `Student` |
| Polymorphism | `Refreshable` (DashboardFrame tab change), abstract `getRole()` |
| Method overloading / constructors | `Student` constructors, `Person` constructors |
| Exception handling | try / catch / multi-catch / try-with-resources / `throws` in every DAO and screen |
| Custom exceptions | `exceptions/` - 3 classes |
| Java library | `ArrayList`, `HashMap`, `Collections.sort`, `Properties`, `java.util.prefs.Preferences`, `MessageDigest`, `LocalDateTime`, `SimpleDateFormat`, `Pattern` |
| Strings | `Validator` (trim, matches, regex), `Person.getInitials()` (split, charAt, StringBuilder), `UIHelper.toTitleCase()`, `String.format` |
| Singleton | `Session` |

**Review 3 - threads, JDBC, Swing application**

| Concept | Where |
|---|---|
| Thread using `Runnable` | `ClockThread` (live clock) |
| Thread using `extends Thread` | `NotificationThread` (daemon thread, checks database) |
| Thread safety with Swing | `SwingUtilities.invokeLater()` in both threads, `volatile` stop flags, `interrupt()` |
| JDBC | `DBConnection`, all DAO classes, `PreparedStatement`, `ResultSet`, `JOIN` queries, generated keys |
| Swing components | see list below |

**Swing components used:** JFrame, JDialog, JPanel, JLabel, JButton, JTextField, JPasswordField,
JFormattedTextField (phone mask), JTextArea, JEditorPane (HTML help), JCheckBox, JRadioButton + ButtonGroup,
JComboBox, JList, JTable (+ sorter, custom renderer), JSpinner, JSlider, JProgressBar, JToggleButton, JTabbedPane,
JSplitPane, JScrollPane, JTree, JMenuBar / JMenu / JMenuItem / JCheckBoxMenuItem, JPopupMenu, JOptionPane, JFileChooser,
tool tips.
**Layouts:** BorderLayout, GridLayout, FlowLayout, GridBagLayout, BoxLayout.
**Events:** ActionListener, MouseAdapter, WindowAdapter, ItemListener, ChangeListener, DocumentListener, ListSelectionListener, TreeSelectionListener, key accelerators (Ctrl+P, F5).

## 6. Possible viva questions (short answers)

* **Why an interface `CrudDAO<T>`?** Every DAO must have the same 5 methods; the generic `T` lets one interface work for all models.
* **Interface vs abstract class?** Interface = only a contract (what to do); abstract class (`Person`) can also hold fields and common code.
* **Why SHA-256 for passwords?** Passwords should not be stored as plain text; only the hash is saved and compared.
* **Why `PreparedStatement`?** Prevents SQL injection and is faster for repeated queries.
* **Why `SwingUtilities.invokeLater`?** Swing is not thread-safe; all GUI changes must run on the Event Dispatch Thread.
* **Why `setDaemon(true)` for NotificationThread?** A daemon thread does not stop the application from exiting.
* **Why try-with-resources?** Connection, statement and result set are closed automatically even if an exception occurs.
* **What is a checked custom exception?** `InvalidInputException extends Exception` - the compiler forces the caller to handle it.
* **How is polymorphism used?** `Component selected = tabs.getSelectedComponent(); if (selected instanceof Refreshable) ((Refreshable) selected).refreshData();` - each tab reloads itself in its own way.
* **Which design pattern did you use?** Singleton (`Session`), DAO pattern, Observer/callback (`NotificationListener`).

## 7. If your Review-1 database is different

This project uses its own `schema.sql`. If you want to keep your Review-1 tables, change the SQL strings
inside the `dao` classes (only the DAO classes contain SQL) and keep the model classes the same.

## 8. Troubleshooting

* `MySQL driver not found` -> jar is missing in `lib/`.
* `Access denied` / `Communications link failure` -> wrong user/password in `db.properties` or MySQL is not running.
* `Unknown database` -> run `sql/schema.sql` first.
* Package error in IDE -> make sure `src` is marked as the source root.
