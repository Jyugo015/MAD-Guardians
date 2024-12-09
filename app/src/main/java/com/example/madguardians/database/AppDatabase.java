package com.example.madguardians.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

//@Database(entities = {User.class, DomainInterested.class}, version = 1)
@Database(entities = {Achievement.class, Appointment.class, Badge.class, ChatHistory.class,
        Collection.class, Course.class, Comment.class, Counselor.class, CounselorAvailability.class,
        Domain.class, DomainInterested.class, Folder.class, Helpdesk.class, Issue.class,
        MediaRead.class, Media.class, MediaSet.class, Notification.class, Post.class, Quiz.class,
        QuestionOption.class, QuizHistory.class, QuizOld.class, QuizQuestion.class,
        QuizResult.class, Staff.class, Timeslot.class, User.class, UserHistory.class,
        VerEducator.class, VerPost.class}, version = 3)
public abstract class AppDatabase extends RoomDatabase {

    public abstract AchievementDao achievementDao();
    public abstract AppointmentDao appointmentDao();
    public abstract BadgeDao badgeDao();
    public abstract ChatHistoryDao chatHistoryDao();
    public abstract CollectionDao collectionDao();
    public abstract CommentDao commentDao();
    public abstract CourseDao courseDao();
    public abstract CounselorDao counselorDao();
    public abstract CounselorAvailabilityDao counselorAvailabilityDao();
    public abstract DomainDao domainDao();
    public abstract DomainInterestedDao domainInterestedDao();
    public abstract FolderDao folderDao();
    public abstract HelpdeskDao helpdeskDao();
    public abstract IssueDao issueDao();
    public abstract MediaReadDao mediaReadDao();
    public abstract MediaSetDao mediaSetDao();
    public abstract MediaDao mediaDao();
    public abstract NotificationDao notificationDao();
    public abstract PostDao postDao();
    public abstract QuestionOptionDao questionOptionDao();
    public abstract QuizDao quizDao();
    public abstract QuizHistoryDao quizHistoryDao();
    public abstract QuizOldDao quizOldDao();
    public abstract QuizQuestionDao quizQuestionDao();
    public abstract QuizResultDao quizResultDao();
    public abstract StaffDao staffDao();
    public abstract TimeslotDao timeslotDao();
    public abstract UserDao userDao();
    public abstract UserHistoryDao userHistoryDao();
    public abstract VerEducatorDao verEducatorDao();
    public abstract VerPostDao verPostDao();

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "app_database")
                            .addMigrations(AppDatabase.MIGRATION_2_3)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    public static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            // Enable foreign key support
            database.execSQL("PRAGMA foreign_keys=ON;");

            //achievement
            database.execSQL(
                    "CREATE TABLE  achievement_new (" +
                            "userId TEXT NOT NULL, " +
                            "badgeId TEXT NOT NULL, " +
                            "PRIMARY KEY(userId, badgeId), " +
                            "FOREIGN KEY(userId) REFERENCES user(userId) ON DELETE CASCADE, " +
                            "FOREIGN KEY(badgeId) REFERENCES badge(badgeId) ON DELETE CASCADE" +
                            ");"
            );

            database.execSQL("DROP TABLE achievement;");
            database.execSQL("ALTER TABLE achievement_new RENAME TO achievement;");

            //appointment
            database.execSQL("CREATE TABLE IF NOT EXISTS appointment_new (" +
                    "counselorAvailabilityId TEXT NOT NULL UNIQUE, " +
                    "userId TEXT NOT NULL, " +
                    "isOnline INTEGER NOT NULL DEFAULT 1, " +
                    "PRIMARY KEY (counselorAvailabilityId, userId), " +
                    "FOREIGN KEY (counselorAvailabilityId) REFERENCES counselorAvailability(counselorAvailabilityId) ON DELETE RESTRICT, " +
                    "FOREIGN KEY (userId) REFERENCES user(userId) ON DELETE CASCADE);");

            database.execSQL("DROP TABLE appointment;");
            database.execSQL("ALTER TABLE appointment_new RENAME TO appointment;");

            //update the isBooked column in counselorAvailability once a new appointment is made
            database.execSQL(
                    "CREATE TRIGGER update_isBooked_on_appointment_insert " +
                            "AFTER INSERT ON appointment " +
                            "FOR EACH ROW " +
                            "BEGIN " +
                            "    UPDATE counselorAvailability " +
                            "    SET isBooked = 1 " +
                            "    WHERE counselorAvailabilityId = NEW.counselorAvailabilityId; " +
                            "END;"
            );

            //badge
            database.execSQL("CREATE TABLE IF NOT EXISTS badge_new (" +
                    "badgeId TEXT PRIMARY KEY, " +
                    "badgeName TEXT NOT NULL UNIQUE, " +
                    "badgeImage TEXT NOT NULL UNIQUE);");

            database.execSQL("DROP TABLE badge;");
            database.execSQL("ALTER TABLE badge_new RENAME TO badge;");

            //chatHistory
            database.execSQL("CREATE TABLE IF NOT EXISTS chatHistory_new (" +
                    "messageId TEXT PRIMARY KEY, " +
                    "senderUserId TEXT, " +
                    "recipientUserId TEXT, " +
                    "senderCounselorId TEXT, " +
                    "recipientCounselorId TEXT, " +
                    "message TEXT NOT NULL, " +
                    "mediaId TEXT, " +
                    "replyMessage TEXT, " +
                    "deliveredTime TEXT, " +
                    "readTime TEXT, " +
                    "FOREIGN KEY (senderUserId) REFERENCES user(userId) ON DELETE SET NULL, " +
                    "FOREIGN KEY (recipientUserId) REFERENCES user(userId) ON DELETE SET NULL, " +
                    "FOREIGN KEY (senderCounselorId) REFERENCES counselor(counselorId) ON DELETE SET NULL, " +
                    "FOREIGN KEY (recipientCounselorId) REFERENCES counselor(counselorId) ON DELETE SET NULL, " +
                    "FOREIGN KEY (mediaId) REFERENCES mediaSet(mediaSetId) ON DELETE SET NULL, " +
                    "FOREIGN KEY (replyMessage) REFERENCES chatHistory(messageId) ON DELETE SET NULL);");

            database.execSQL("DROP TABLE chatHistory;");
            database.execSQL("ALTER TABLE chatHistory_new RENAME TO badge;");

            //collection
            database.execSQL(
                    "CREATE TABLE collection_new (" +
                            "collectionId TEXT PRIMARY KEY, " +
                            "userId TEXT NOT NULL, " +
                            "postId TEXT, " +
                            "courseId TEXT, " +
                            "folderId TEXT, " +
                            "FOREIGN KEY(userId) REFERENCES user(userId) ON DELETE CASCADE, " +
                            "FOREIGN KEY(postId) REFERENCES post(postId) ON DELETE CASCADE, " +
                            "FOREIGN KEY(courseId) REFERENCES course(courseId) ON DELETE CASCADE, " +
                            "FOREIGN KEY(folderId) REFERENCES folder(folderId) ON DELETE SET NULL," +
                            "CHECK (postId IS NOT NULL OR courseId IS NOT NULL)" +
                            ");"
            );

            database.execSQL("DROP TABLE collection;");
            database.execSQL("ALTER TABLE collection_new RENAME TO collection;");

            //comment
            database.execSQL(
                    "CREATE TABLE comment_new (" +
                            "commentId TEXT PRIMARY KEY, " +
                            "userId TEXT, " +
                            "postId TEXT NOT NULL, " +
                            "comment TEXT NOT NULL, " +
                            "rootComment TEXT, " +
                            "replyUserId TEXT, " +
                            "isRead INTEGER NOT NULL DEFAULT 0, " +
                            "timestamp TEXT NOT NULL, " +
                            "FOREIGN KEY(userId) REFERENCES user(userId) ON DELETE SET NULL, " +
                            "FOREIGN KEY(postId) REFERENCES post(postId) ON DELETE CASCADE, " +
                            "FOREIGN KEY(rootComment) REFERENCES comment(commentId) ON DELETE CASCADE, " +
                            "FOREIGN KEY(replyUserId) REFERENCES user(userId) ON DELETE SET NULL" +
                            ");"
            );

            database.execSQL("DROP TABLE comment;");
            database.execSQL("ALTER TABLE comment_new RENAME TO comment;");

            //counselor
            database.execSQL(
                    "CREATE TABLE counselor_new (" +
                            "counselorId TEXT PRIMARY KEY, " +
                            "name TEXT NOT NULL, " +
                            "office TEXT NOT NULL, " +
                            "email TEXT NOT NULL UNIQUE, " +
                            "password TEXT NOT NULL, " +
                            "profilePic TEXT NOT NULL DEFAULT 'url link of the default profilepic', " +
                            "contactNo TEXT NOT NULL UNIQUE" +
                            ");"
            );
            database.execSQL("DROP TABLE counselor;");
            database.execSQL("ALTER TABLE counselor_new RENAME TO counselor;");

            //counselorAvailability
            database.execSQL(
                    "CREATE TABLE counselorAvailability_new (" +
                            "counselorAvailabilityId TEXT PRIMARY KEY, " +
                            "counselorId TEXT NOT NULL, " +
                            "timeslotId TEXT NOT NULL, " +
                            "date TEXT NOT NULL, " +
                            "isBooked INTEGER NOT NULL DEFAULT 0, " +
                            "FOREIGN KEY(counselorId) REFERENCES counselor(counselorId) ON DELETE CASCADE, " +
                            "FOREIGN KEY(timeslotId) REFERENCES timeslot(timeslotId) ON DELETE RESTRICT" +
                            ");"
            );
            database.execSQL("DROP TABLE counselorAvailability;");
            database.execSQL("ALTER TABLE counselorAvailability_new RENAME TO counselorAvailability;");

            //course
            database.execSQL(
                    "CREATE TABLE course_new (" +
                            "courseId TEXT PRIMARY KEY, " +
                            "title TEXT NOT NULL, " +
                            "description TEXT NOT NULL, " +
                            "coverImage TEXT NOT NULL, " +
                            "post1 TEXT, " +
                            "post2 TEXT, " +
                            "post3 TEXT, " +
                            "folderId TEXT, " +
                            "date TEXT NOT NULL, " +
                            "FOREIGN KEY(post1) REFERENCES post(postId) ON DELETE SET NULL, " +
                            "FOREIGN KEY(post2) REFERENCES post(postId) ON DELETE SET NULL, " +
                            "FOREIGN KEY(post3) REFERENCES post(postId) ON DELETE SET NULL, " +
                            "FOREIGN KEY(folderId) REFERENCES folder(folderId) ON DELETE SET NULL" +
                            ");"
            );

            database.execSQL("DROP TABLE course;");
            database.execSQL("ALTER TABLE course_new RENAME TO course;");

            //domain
            database.execSQL(
                    "CREATE TABLE domain_new (" +
                            "domainId TEXT PRIMARY KEY, " +
                            "domainName TEXT NOT NULL UNIQUE " +
                            ");"
            );
            database.execSQL("DROP TABLE domain;");
            database.execSQL("ALTER TABLE domain_new RENAME TO domain;");

            // domainInterested
            database.execSQL("CREATE TABLE domainInterested_new (" +
                    "userId TEXT NOT NULL, " +
                    "domainId TEXT NOT NULL, " +
                    "PRIMARY KEY(userId, domainId), " +
                    "FOREIGN KEY(userId) REFERENCES user(userId) ON DELETE CASCADE, " +
                    "FOREIGN KEY(domainId) REFERENCES domain(domainId) ON DELETE CASCADE);");

            database.execSQL("DROP TABLE IF EXISTS domainInterested;");
            database.execSQL("ALTER TABLE domainInterested_new RENAME TO domainInterested;");

            //folder
            database.execSQL(
                    "CREATE TABLE folder_new (" +
                            "folderId TEXT PRIMARY KEY, " +
                            "userId TEXT NOT NULL, " +
                            "name TEXT NOT NULL, " +
                            "rootFolder TEXT, " +
                            "FOREIGN KEY(userId) REFERENCES user(userId) ON DELETE CASCADE, " +
                            "FOREIGN KEY(rootFolder) REFERENCES folder(folderId) ON DELETE SET NULL" +
                            ");"
            );

            database.execSQL("DROP TABLE IF EXISTS folder;");
            database.execSQL("ALTER TABLE folder_new RENAME TO folder;");

            //helpdesk
            database.execSQL(
                    "CREATE TABLE helpdesk_new (" +
                            "helpdeskId TEXT PRIMARY KEY, " +
                            "issueId TEXT NOT NULL, " +
                            "userId TEXT NOT NULL, " +
                            "postId TEXT, " +
                            "courseId TEXT, " +
                            "commentId TEXT, " +
                            "quizId TEXT, " +
                            "staffId TEXT NOT NULL, " +
                            "reason TEXT, " +
                            "helpdeskStatus TEXT NOT NULL DEFAULT 'pending', " +
                            "FOREIGN KEY(issueId) REFERENCES issue(issueId) ON DELETE CASCADE, " +
                            "FOREIGN KEY(userId) REFERENCES user(userId) ON DELETE CASCADE, " +
                            "FOREIGN KEY(postId) REFERENCES post(postId) ON DELETE SET NULL, " +
                            "FOREIGN KEY(courseId) REFERENCES course(courseId) ON DELETE SET NULL, " +
                            "FOREIGN KEY(commentId) REFERENCES comment(commentId) ON DELETE SET NULL, " +
                            "FOREIGN KEY(quizId) REFERENCES quizQuestion(quizId) ON DELETE SET NULL, " +
                            "FOREIGN KEY(staffId) REFERENCES staff(staffId) ON DELETE RESTRICT," +
                            "CHECK (postId IS NOT NULL OR courseId IS NOT NULL OR commentId IS NOT NULL OR quizId IS NOT NULL OR helpdeskStatus = 'deleting')" +
                            ");"
            );
            database.execSQL("DROP TABLE IF EXISTS helpdesk;");
            database.execSQL("ALTER TABLE helpdesk_new RENAME TO helpdesk;");

            // Add the trigger to delete records when all four columns are NULL
            database.execSQL(
                    "CREATE TRIGGER delete_helpdesk_when_all_null " +
                            "AFTER UPDATE OF postId, courseId, commentId, quizId ON helpdesk " +
                            "FOR EACH ROW " +
                            "WHEN NEW.postId IS NULL AND NEW.courseId IS NULL AND NEW.commentId IS NULL AND NEW.quizId IS NULL " +
                            "BEGIN " +
                            "    DELETE FROM helpdesk WHERE helpdeskId = NEW.helpdeskId; " +
                            "END;"
            );

            //issue
            database.execSQL(
                    "CREATE TABLE issue_new (" +
                            "issueId TEXT PRIMARY KEY, " +
                            "type TEXT NOT NULL " +
                            ");"
            );

            database.execSQL("DROP TABLE IF EXISTS issue;");
            database.execSQL("ALTER TABLE issue_new RENAME TO issue;");

            //mediaRead
            database.execSQL(
                    "CREATE TABLE mediaRead_new (" +
                            "mediaId TEXT NOT NULL, " +
                            "postId TEXT NOT NULL, " +
                            "userId TEXT NOT NULL, " +
                            "PRIMARY KEY(mediaId, postId, userId), " +
                            "FOREIGN KEY(mediaId) REFERENCES mediaSet(mediaId) ON DELETE CASCADE, " +
                            "FOREIGN KEY(postId) REFERENCES post(postId) ON DELETE CASCADE, " +
                            "FOREIGN KEY(userId) REFERENCES user(userId) ON DELETE CASCADE" +
                            ");"
            );
            database.execSQL("DROP TABLE IF EXISTS mediaRead;");
            database.execSQL("ALTER TABLE mediaRead_new RENAME TO mediaRead;");

            //media
            database.execSQL(
                    "CREATE TABLE media_new (" +
                            "mediaId TEXT PRIMARY KEY, " +
                            "mediaSetId TEXT NOT NULL, " +
                            "url TEXT NOT NULL, " +
                            "FOREIGN KEY(mediaSetId) REFERENCES mediaSet(mediaSetId) ON DELETE CASCADE" +
                            ");"
            );
            database.execSQL("DROP TABLE IF EXISTS media;");
            database.execSQL("ALTER TABLE media_new RENAME TO media;");

            //mediaSet
            database.execSQL(
                    "CREATE TABLE mediaSet_new (" +
                            "mediaSetId TEXT PRIMARY KEY " +
                            ");"
            );
            database.execSQL("DROP TABLE IF EXISTS mediaSet;");
            database.execSQL("ALTER TABLE mediaSet_new RENAME TO mediaSet;");

            //notification
            database.execSQL(
                    "CREATE TABLE notification_new (" +
                            "notificationId TEXT PRIMARY KEY, " +
                            "userId TEXT NOT NULL, " +
                            "message TEXT NOT NULL, " +
                            "deliveredTime TEXT, " +
                            "readTime TEXT, " +
                            "FOREIGN KEY(userId) REFERENCES user(userId) ON DELETE CASCADE" +
                            ");"
            );
            database.execSQL("DROP TABLE IF EXISTS notification;");
            database.execSQL("ALTER TABLE notification_new RENAME TO notification;");

            //post
            database.execSQL(
                    "CREATE TABLE post_new (" +
                            "postId TEXT PRIMARY KEY, " +
                            "userId TEXT NOT NULL, " +
                            "title TEXT NOT NULL, " +
                            "description TEXT NOT NULL, " +
                            "imageSetId TEXT, " +
                            "videoSetId TEXT, " +
                            "fileSetId TEXT, " +
                            "quizId TEXT, " +
                            "domainId TEXT NOT NULL, " +
                            "folderId TEXT, " +
                            "date TEXT NOT NULL, " +
                            "FOREIGN KEY(userId) REFERENCES user(userId) ON DELETE CASCADE, " +
                            "FOREIGN KEY(imageSetId) REFERENCES mediaSet(mediaSetId) ON DELETE SET NULL, " +
                            "FOREIGN KEY(videoSetId) REFERENCES mediaSet(mediaSetId) ON DELETE SET NULL, " +
                            "FOREIGN KEY(fileSetId) REFERENCES mediaSet(mediaSetId) ON DELETE SET NULL, " +
                            "FOREIGN KEY(quizId) REFERENCES quiz(quizId) ON DELETE SET NULL, " +
                            "FOREIGN KEY(domainId) REFERENCES domain(domainId) ON DELETE RESTRICT, " +
                            "FOREIGN KEY(folderId) REFERENCES folder(folderId) ON DELETE SET NULL," +
                            "CHECK (imageSetId IS NOT NULL OR videoSetId IS NOT NULL OR fileSetId IS NOT NULL OR quizId IS NOT NULL)" +
                            ");"
            );
            database.execSQL("DROP TABLE IF EXISTS post;");
            database.execSQL("ALTER TABLE post_new RENAME TO post;");

            //questionOption
            database.execSQL(
                    "CREATE TABLE questionOption_new (" +
                            "questionId TEXT NOT NULL, " +
                            "choice TEXT NOT NULL, " +
                            "isCorrect INTEGER NOT NULL DEFAULT 0, " +
                            "PRIMARY KEY(questionId, choice), " +
                            "FOREIGN KEY(questionId) REFERENCES quizQuestion(questionId) ON DELETE CASCADE" +
                            ");"
            );

            database.execSQL("DROP TABLE IF EXISTS questionOption;");
            database.execSQL("ALTER TABLE questionOption_new RENAME TO questionOption;");

            //quiz
            database.execSQL(
                    "CREATE TABLE quiz_new (" +
                            "quizId TEXT PRIMARY KEY " +
                            ");"
            );
            database.execSQL("DROP TABLE IF EXISTS quiz;");
            database.execSQL("ALTER TABLE quiz_new RENAME TO quiz;");

            //quizHistory
            database.execSQL(
                    "CREATE TABLE quizHistory_new (" +
                            "quizId TEXT PRIMARY KEY, " +
                            "userId TEXT NOT NULL, " +
                            "score INTEGER NOT NULL, " +
                            "timestamp TEXT NOT NULL, " +
                            "FOREIGN KEY(quizId) REFERENCES quiz(quizId) ON DELETE CASCADE, " +
                            "FOREIGN KEY(userId) REFERENCES user(userId) ON DELETE CASCADE" +
                            ");"
            );
            database.execSQL("DROP TABLE IF EXISTS quizHistory;");
            database.execSQL("ALTER TABLE quizHistory_new RENAME TO quizHistory;");

            //quizOld
            database.execSQL(
                    "CREATE TABLE quizOld_new (" +
                            "quizId TEXT NOT NULL, " +
                            "postId TEXT NOT NULL, " +
                            "PRIMARY KEY(quizId, postId), " +
                            "FOREIGN KEY(quizId) REFERENCES quiz(quizId) ON DELETE CASCADE, " +
                            "FOREIGN KEY(postId) REFERENCES post(postId) ON DELETE CASCADE" +
                            ");"
            );
            database.execSQL("DROP TABLE IF EXISTS quizOld;");
            database.execSQL("ALTER TABLE quizOld_new RENAME TO quizOld;");

            //quizQuestion
            database.execSQL(
                    "CREATE TABLE IF NOT EXISTS quizQuestion_new (" +
                            "questionId TEXT PRIMARY KEY, " +
                            "quizId TEXT NOT NULL, " +
                            "question TEXT NOT NULL, " +
                            "questionNo INTEGER NOT NULL," +
                            "FOREIGN KEY(quizId) REFERENCES quiz(quizId) ON DELETE CASCADE " +
                            ");"
            );
            database.execSQL("DROP TABLE IF EXISTS quizQuestion;");
            database.execSQL("ALTER TABLE quizQuestion_new RENAME TO quizQestion;");

            //quizResult
            database.execSQL(
                    "CREATE TABLE IF NOT EXISTS quizResult_new (" +
                            "quizResultId TEXT PRIMARY KEY, " +
                            "questionId TEXT NOT NULL, " +
                            "userId TEXT NOT NULL, " +
                            "userAns TEXT NOT NULL, " +
                            "timestamp TEXT NOT NULL, " +
                            "FOREIGN KEY(questionId) REFERENCES quizQuestion(questionId) ON DELETE CASCADE, " +
                            "FOREIGN KEY(userId) REFERENCES user(userId) ON DELETE CASCADE, " +
                            "FOREIGN KEY(userAns) REFERENCES questionOption(choice) ON DELETE CASCADE" +
                            ");"
            );

            database.execSQL("DROP TABLE IF EXISTS quizResult;");
            database.execSQL("ALTER TABLE quizResult_new RENAME TO quizResult;");

            //staff
            database.execSQL(
                    "CREATE TABLE IF NOT EXISTS staff_new (" +
                            "staffId TEXT PRIMARY KEY, " +
                            "name TEXT NOT NULL UNIQUE, " +
                            "email TEXT NOT NULL UNIQUE, " +
                            "password TEXT NOT NULL " +
                            ");"
            );

            database.execSQL("DROP TABLE IF EXISTS staff;");

            database.execSQL("ALTER TABLE staff_new RENAME TO staff;");

            //timeslot
            database.execSQL(
                    "CREATE TABLE IF NOT EXISTS timeslot_new (" +
                            "timeslotId TEXT PRIMARY KEY, " +
                            "startTime INTEGER NOT NULL, " +
                            "endTime INTEGER NOT NULL " +
                            ");"
            );
            database.execSQL("DROP TABLE IF EXISTS timeslot;");
            database.execSQL("ALTER TABLE timeslot_new RENAME TO timeslot;");

            // user
            database.execSQL("CREATE TABLE user_new (" +
                    "userId TEXT PRIMARY KEY, " +
                    "name TEXT NOT NULL DEFAULT 'bookworm', " +
                    "email TEXT NOT NULL UNIQUE, " +
                    "phoneNo TEXT NOT NULL UNIQUE, " +
                    "password TEXT NOT NULL, " +
                    "profilePic TEXT NOT NULL DEFAULT 'default_profile_pic_url', " +
                    "lastLogin TEXT NOT NULL, " +
                    "strikeLoginDays INTEGER NOT NULL);");

            // Drop the old tables (assuming there is no data migration needed)
            database.execSQL("DROP TABLE IF EXISTS user;");

            // Rename new tables to the original table names
            database.execSQL("ALTER TABLE user_new RENAME TO user;");

            //check if the username is unique before inserting or updating the username other than "bookworm"
            database.execSQL(
                    "CREATE TRIGGER check_unique_username " +
                            "BEFORE INSERT ON user " +
                            "WHEN NEW.name != 'bookworm' " + // Only check non-default usernames
                            "BEGIN " +
                            "    SELECT RAISE(FAIL, 'Username already exists') " +
                            "    WHERE (SELECT COUNT(*) FROM user WHERE name = NEW.name) > 0; " +
                            "END;"
            );

            // Add a trigger for updates
            database.execSQL(
                    "CREATE TRIGGER check_unique_username_update " +
                            "BEFORE UPDATE ON user " +
                            "WHEN NEW.name != 'bookworm' AND OLD.name != NEW.name " + // Only check when username changes
                            "BEGIN " +
                            "    SELECT RAISE(FAIL, 'Username already exists') " +
                            "    WHERE (SELECT COUNT(*) FROM user WHERE name = NEW.name) > 0; " +
                            "END;"
            );

            // Add a query to update streak counts for all users
            database.execSQL(
                    "UPDATE user " +
                            "SET " +
                            "    strikeLoginDays = CASE " +
                            "        WHEN lastLogin = date('now', '-1 day') THEN strikeLoginDays + 1 " +
                            "        ELSE 1 " +
                            "    END, " +
                            "    lastLogin = date('now')"
            );

            //userHistory
            database.execSQL(
                    "CREATE TABLE IF NOT EXISTS userHistory_new (" +
                            "postId TEXT NOT NULL, " +
                            "userId TEXT NOT NULL, " +
                            "progress INTEGER NOT NULL DEFAULT 1, " +
                            "time TEXT NOT NULL, " +
                            "PRIMARY KEY(postId, userId), " +
                            "FOREIGN KEY(postId) REFERENCES post(postId) ON DELETE CASCADE, " +
                            "FOREIGN KEY(userId) REFERENCES user(userId) ON DELETE CASCADE" +
                            ");"
            );
            database.execSQL("DROP TABLE IF EXISTS userHistory;");
            database.execSQL("ALTER TABLE userHistory_new RENAME TO userHistory;");

            //verEducator
            database.execSQL(
                    "CREATE TABLE IF NOT EXISTS verEducator_new (" +
                            "userId TEXT NOT NULL, " +
                            "imageSetId TEXT UNIQUE, " +
                            "fileSetId TEXT UNIQUE, " +
                            "domainId TEXT NOT NULL, " +
                            "staffId TEXT NOT NULL, " +
                            "verifiedStatus TEXT NOT NULL DEFAULT 'pending', " +
                            "PRIMARY KEY(userId, domainId), " +
                            "FOREIGN KEY(userId) REFERENCES user(userId) ON DELETE CASCADE, " +
                            "FOREIGN KEY(domainId) REFERENCES domain(domainId) ON DELETE CASCADE, " +
                            "FOREIGN KEY(staffId) REFERENCES staff(staffId) ON DELETE RESTRICT, " +
                            "FOREIGN KEY(imageSetId) REFERENCES mediaSet(mediaSetId) ON DELETE SET NULL, " +
                            "FOREIGN KEY(fileSetId) REFERENCES mediaSet(mediaSetId) ON DELETE SET NULL " +
                            ");"
            );
            database.execSQL("DROP TABLE IF EXISTS verEducator;");
            database.execSQL("ALTER TABLE verEducator_new RENAME TO verEducator;");

            //verPost
            database.execSQL(
                    "CREATE TABLE IF NOT EXISTS verPost_new (" +
                            "verPostId TEXT PRIMARY KEY, " +
                            "postId TEXT NOT NULL UNIQUE, " +
                            "staffId TEXT NOT NULL, " +
                            "verifiedStatus TEXT NOT NULL DEFAULT 'pending', " +
                            "FOREIGN KEY(postId) REFERENCES post(postId) ON DELETE CASCADE, " +
                            "FOREIGN KEY(staffId) REFERENCES staff(staffId) ON DELETE RESTRICT " +
                            ");"
            );

            database.execSQL("DROP TABLE IF EXISTS verPost;");

            database.execSQL("ALTER TABLE verPost_new RENAME TO verPost;");
        }
    };

    public static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            // Enable foreign key support
            database.execSQL("PRAGMA foreign_keys=ON;");

            //achievement
            database.execSQL(
                    "CREATE TABLE  achievement_new (" +
                            "userId TEXT NOT NULL, " +
                            "badgeId TEXT NOT NULL, " +
                            "PRIMARY KEY(userId, badgeId), " +
                            "FOREIGN KEY(userId) REFERENCES user(userId) ON DELETE CASCADE, " +
                            "FOREIGN KEY(badgeId) REFERENCES badge(badgeId) ON DELETE CASCADE" +
                            ");"
            );

            database.execSQL("DROP TABLE achievement;");
            database.execSQL("ALTER TABLE achievement_new RENAME TO achievement;");

            //appointment
            database.execSQL("CREATE TABLE IF NOT EXISTS appointment_new (" +
                    "counselorAvailabilityId TEXT NOT NULL UNIQUE, " +
                    "userId TEXT NOT NULL, " +
                    "isOnline INTEGER NOT NULL DEFAULT 1, " +
                    "PRIMARY KEY (counselorAvailabilityId, userId), " +
                    "FOREIGN KEY (counselorAvailabilityId) REFERENCES counselorAvailability(counselorAvailabilityId) ON DELETE RESTRICT, " +
                    "FOREIGN KEY (userId) REFERENCES user(userId) ON DELETE CASCADE);");

            database.execSQL("DROP TABLE appointment;");
            database.execSQL("ALTER TABLE appointment_new RENAME TO appointment;");

            //update the isBooked column in counselorAvailability once a new appointment is made
            database.execSQL(
                    "CREATE TRIGGER update_isBooked_on_appointment_insert " +
                            "AFTER INSERT ON appointment " +
                            "FOR EACH ROW " +
                            "BEGIN " +
                            "    UPDATE counselorAvailability " +
                            "    SET isBooked = 1 " +
                            "    WHERE counselorAvailabilityId = NEW.counselorAvailabilityId; " +
                            "END;"
            );

            //badge
            database.execSQL("CREATE TABLE IF NOT EXISTS badge_new (" +
                    "badgeId TEXT PRIMARY KEY, " +
                    "badgeName TEXT NOT NULL UNIQUE, " +
                    "badgeImage TEXT NOT NULL UNIQUE);");

            database.execSQL("DROP TABLE badge;");
            database.execSQL("ALTER TABLE badge_new RENAME TO badge;");

            //chatHistory
            database.execSQL("CREATE TABLE IF NOT EXISTS chatHistory_new (" +
                    "messageId TEXT PRIMARY KEY, " +
                    "senderUserId TEXT, " +
                    "recipientUserId TEXT, " +
                    "senderCounselorId TEXT, " +
                    "recipientCounselorId TEXT, " +
                    "message TEXT NOT NULL, " +
                    "mediaId TEXT, " +
                    "replyMessage TEXT, " +
                    "deliveredTime TEXT, " +
                    "readTime TEXT, " +
                    "FOREIGN KEY (senderUserId) REFERENCES user(userId) ON DELETE SET NULL, " +
                    "FOREIGN KEY (recipientUserId) REFERENCES user(userId) ON DELETE SET NULL, " +
                    "FOREIGN KEY (senderCounselorId) REFERENCES counselor(counselorId) ON DELETE SET NULL, " +
                    "FOREIGN KEY (recipientCounselorId) REFERENCES counselor(counselorId) ON DELETE SET NULL, " +
                    "FOREIGN KEY (mediaId) REFERENCES mediaSet(mediaSetId) ON DELETE SET NULL, " +
                    "FOREIGN KEY (replyMessage) REFERENCES chatHistory(messageId) ON DELETE SET NULL);");

            database.execSQL("DROP TABLE chatHistory;");
            database.execSQL("ALTER TABLE chatHistory_new RENAME TO badge;");

            //collection
            database.execSQL(
                    "CREATE TABLE collection_new (" +
                            "collectionId TEXT PRIMARY KEY, " +
                            "userId TEXT NOT NULL, " +
                            "postId TEXT, " +
                            "courseId TEXT, " +
                            "folderId TEXT, " +
                            "FOREIGN KEY(userId) REFERENCES user(userId) ON DELETE CASCADE, " +
                            "FOREIGN KEY(postId) REFERENCES post(postId) ON DELETE CASCADE, " +
                            "FOREIGN KEY(courseId) REFERENCES course(courseId) ON DELETE CASCADE, " +
                            "FOREIGN KEY(folderId) REFERENCES folder(folderId) ON DELETE SET NULL," +
                            "CHECK (postId IS NOT NULL OR courseId IS NOT NULL)" +
                            ");"
            );

            database.execSQL("DROP TABLE collection;");
            database.execSQL("ALTER TABLE collection_new RENAME TO collection;");

            //comment
            database.execSQL(
                    "CREATE TABLE comment_new (" +
                            "commentId TEXT PRIMARY KEY, " +
                            "userId TEXT, " +
                            "postId TEXT NOT NULL, " +
                            "comment TEXT NOT NULL, " +
                            "rootComment TEXT, " +
                            "replyUserId TEXT, " +
                            "isRead INTEGER NOT NULL DEFAULT 0, " +
                            "timestamp TEXT NOT NULL, " +
                            "FOREIGN KEY(userId) REFERENCES user(userId) ON DELETE SET NULL, " +
                            "FOREIGN KEY(postId) REFERENCES post(postId) ON DELETE CASCADE, " +
                            "FOREIGN KEY(rootComment) REFERENCES comment(commentId) ON DELETE CASCADE, " +
                            "FOREIGN KEY(replyUserId) REFERENCES user(userId) ON DELETE SET NULL" +
                            ");"
            );

            database.execSQL("DROP TABLE comment;");
            database.execSQL("ALTER TABLE comment_new RENAME TO comment;");

            //counselor
            database.execSQL(
                    "CREATE TABLE counselor_new (" +
                            "counselorId TEXT PRIMARY KEY, " +
                            "name TEXT NOT NULL, " +
                            "office TEXT NOT NULL, " +
                            "email TEXT NOT NULL UNIQUE, " +
                            "password TEXT NOT NULL, " +
                            "profilePic TEXT NOT NULL DEFAULT 'url link of the default profilepic', " +
                            "contactNo TEXT NOT NULL UNIQUE" +
                            ");"
            );
            database.execSQL("DROP TABLE counselor;");
            database.execSQL("ALTER TABLE counselor_new RENAME TO counselor;");

            //counselorAvailability
            database.execSQL(
                    "CREATE TABLE counselorAvailability_new (" +
                            "counselorAvailabilityId TEXT PRIMARY KEY, " +
                            "counselorId TEXT NOT NULL, " +
                            "timeslotId TEXT NOT NULL, " +
                            "date TEXT NOT NULL, " +
                            "isBooked INTEGER NOT NULL DEFAULT 0, " +
                            "FOREIGN KEY(counselorId) REFERENCES counselor(counselorId) ON DELETE CASCADE, " +
                            "FOREIGN KEY(timeslotId) REFERENCES timeslot(timeslotId) ON DELETE RESTRICT" +
                            ");"
            );
            database.execSQL("DROP TABLE counselorAvailability;");
            database.execSQL("ALTER TABLE counselorAvailability_new RENAME TO counselorAvailability;");

            //course
            database.execSQL(
                    "CREATE TABLE course_new (" +
                            "courseId TEXT PRIMARY KEY, " +
                            "title TEXT NOT NULL, " +
                            "description TEXT NOT NULL, " +
                            "coverImage TEXT NOT NULL, " +
                            "post1 TEXT, " +
                            "post2 TEXT, " +
                            "post3 TEXT, " +
                            "folderId TEXT, " +
                            "date TEXT NOT NULL, " +
                            "FOREIGN KEY(post1) REFERENCES post(postId) ON DELETE SET NULL, " +
                            "FOREIGN KEY(post2) REFERENCES post(postId) ON DELETE SET NULL, " +
                            "FOREIGN KEY(post3) REFERENCES post(postId) ON DELETE SET NULL, " +
                            "FOREIGN KEY(folderId) REFERENCES folder(folderId) ON DELETE SET NULL" +
                            ");"
            );

            database.execSQL("DROP TABLE course;");
            database.execSQL("ALTER TABLE course_new RENAME TO course;");

            //domain
            database.execSQL(
                    "CREATE TABLE domain_new (" +
                            "domainId TEXT PRIMARY KEY, " +
                            "domainName TEXT NOT NULL UNIQUE " +
                            ");"
            );
            database.execSQL("DROP TABLE domain;");
            database.execSQL("ALTER TABLE domain_new RENAME TO domain;");

            // domainInterested
            database.execSQL("CREATE TABLE domainInterested_new (" +
                    "userId TEXT NOT NULL, " +
                    "domainId TEXT NOT NULL, " +
                    "PRIMARY KEY(userId, domainId), " +
                    "FOREIGN KEY(userId) REFERENCES user(userId) ON DELETE CASCADE, " +
                    "FOREIGN KEY(domainId) REFERENCES domain(domainId) ON DELETE CASCADE);");

            database.execSQL("DROP TABLE IF EXISTS domainInterested;");
            database.execSQL("ALTER TABLE domainInterested_new RENAME TO domainInterested;");

            //folder
            database.execSQL(
                    "CREATE TABLE folder_new (" +
                            "folderId TEXT PRIMARY KEY, " +
                            "userId TEXT NOT NULL, " +
                            "name TEXT NOT NULL, " +
                            "rootFolder TEXT, " +
                            "FOREIGN KEY(userId) REFERENCES user(userId) ON DELETE CASCADE, " +
                            "FOREIGN KEY(rootFolder) REFERENCES folder(folderId) ON DELETE SET NULL" +
                            ");"
            );

            database.execSQL("DROP TABLE IF EXISTS folder;");
            database.execSQL("ALTER TABLE folder_new RENAME TO folder;");

            //helpdesk
            database.execSQL(
                    "CREATE TABLE helpdesk_new (" +
                            "helpdeskId TEXT PRIMARY KEY, " +
                            "issueId TEXT NOT NULL, " +
                            "userId TEXT NOT NULL, " +
                            "postId TEXT, " +
                            "courseId TEXT, " +
                            "commentId TEXT, " +
                            "quizId TEXT, " +
                            "staffId TEXT NOT NULL, " +
                            "reason TEXT, " +
                            "helpdeskStatus TEXT NOT NULL DEFAULT 'pending', " +
                            "FOREIGN KEY(issueId) REFERENCES issue(issueId) ON DELETE CASCADE, " +
                            "FOREIGN KEY(userId) REFERENCES user(userId) ON DELETE CASCADE, " +
                            "FOREIGN KEY(postId) REFERENCES post(postId) ON DELETE SET NULL, " +
                            "FOREIGN KEY(courseId) REFERENCES course(courseId) ON DELETE SET NULL, " +
                            "FOREIGN KEY(commentId) REFERENCES comment(commentId) ON DELETE SET NULL, " +
                            "FOREIGN KEY(quizId) REFERENCES quizQuestion(quizId) ON DELETE SET NULL, " +
                            "FOREIGN KEY(staffId) REFERENCES staff(staffId) ON DELETE RESTRICT," +
                            "CHECK (postId IS NOT NULL OR courseId IS NOT NULL OR commentId IS NOT NULL OR quizId IS NOT NULL OR helpdeskStatus = 'deleting')" +
                            ");"
            );
            database.execSQL("DROP TABLE IF EXISTS helpdesk;");
            database.execSQL("ALTER TABLE helpdesk_new RENAME TO helpdesk;");

            // Add the trigger to delete records when all four columns are NULL
            database.execSQL(
                    "CREATE TRIGGER delete_helpdesk_when_all_null " +
                            "AFTER UPDATE OF postId, courseId, commentId, quizId ON helpdesk " +
                            "FOR EACH ROW " +
                            "WHEN NEW.postId IS NULL AND NEW.courseId IS NULL AND NEW.commentId IS NULL AND NEW.quizId IS NULL " +
                            "BEGIN " +
                            "    DELETE FROM helpdesk WHERE helpdeskId = NEW.helpdeskId; " +
                            "END;"
            );

            //issue
            database.execSQL(
                    "CREATE TABLE issue_new (" +
                            "issueId TEXT PRIMARY KEY, " +
                            "type TEXT NOT NULL " +
                            ");"
            );

            database.execSQL("DROP TABLE IF EXISTS issue;");
            database.execSQL("ALTER TABLE issue_new RENAME TO issue;");

            //mediaRead
            database.execSQL(
                    "CREATE TABLE mediaRead_new (" +
                            "mediaId TEXT NOT NULL, " +
                            "postId TEXT NOT NULL, " +
                            "userId TEXT NOT NULL, " +
                            "PRIMARY KEY(mediaId, postId, userId), " +
                            "FOREIGN KEY(mediaId) REFERENCES mediaSet(mediaId) ON DELETE CASCADE, " +
                            "FOREIGN KEY(postId) REFERENCES post(postId) ON DELETE CASCADE, " +
                            "FOREIGN KEY(userId) REFERENCES user(userId) ON DELETE CASCADE" +
                            ");"
            );
            database.execSQL("DROP TABLE IF EXISTS mediaRead;");
            database.execSQL("ALTER TABLE mediaRead_new RENAME TO mediaRead;");

            //media
            database.execSQL(
                    "CREATE TABLE media_new (" +
                            "mediaId TEXT PRIMARY KEY, " +
                            "mediaSetId TEXT NOT NULL, " +
                            "url TEXT NOT NULL, " +
                            "FOREIGN KEY(mediaSetId) REFERENCES mediaSet(mediaSetId) ON DELETE CASCADE" +
                            ");"
            );
            database.execSQL("DROP TABLE IF EXISTS media;");
            database.execSQL("ALTER TABLE media_new RENAME TO media;");

            //mediaSet
            database.execSQL(
                    "CREATE TABLE mediaSet_new (" +
                            "mediaSetId TEXT PRIMARY KEY " +
                            ");"
            );
            database.execSQL("DROP TABLE IF EXISTS mediaSet;");
            database.execSQL("ALTER TABLE mediaSet_new RENAME TO mediaSet;");

            //notification
            database.execSQL(
                    "CREATE TABLE notification_new (" +
                            "notificationId TEXT PRIMARY KEY, " +
                            "userId TEXT NOT NULL, " +
                            "message TEXT NOT NULL, " +
                            "deliveredTime TEXT, " +
                            "readTime TEXT, " +
                            "FOREIGN KEY(userId) REFERENCES user(userId) ON DELETE CASCADE" +
                            ");"
            );
            database.execSQL("DROP TABLE IF EXISTS notification;");
            database.execSQL("ALTER TABLE notification_new RENAME TO notification;");

            //post
            database.execSQL(
                    "CREATE TABLE post_new (" +
                            "postId TEXT PRIMARY KEY, " +
                            "userId TEXT NOT NULL, " +
                            "title TEXT NOT NULL, " +
                            "description TEXT NOT NULL, " +
                            "imageSetId TEXT, " +
                            "videoSetId TEXT, " +
                            "fileSetId TEXT, " +
                            "quizId TEXT, " +
                            "domainId TEXT NOT NULL, " +
                            "folderId TEXT, " +
                            "date TEXT NOT NULL, " +
                            "FOREIGN KEY(userId) REFERENCES user(userId) ON DELETE CASCADE, " +
                            "FOREIGN KEY(imageSetId) REFERENCES mediaSet(mediaSetId) ON DELETE SET NULL, " +
                            "FOREIGN KEY(videoSetId) REFERENCES mediaSet(mediaSetId) ON DELETE SET NULL, " +
                            "FOREIGN KEY(fileSetId) REFERENCES mediaSet(mediaSetId) ON DELETE SET NULL, " +
                            "FOREIGN KEY(quizId) REFERENCES quiz(quizId) ON DELETE SET NULL, " +
                            "FOREIGN KEY(domainId) REFERENCES domain(domainId) ON DELETE RESTRICT, " +
                            "FOREIGN KEY(folderId) REFERENCES folder(folderId) ON DELETE SET NULL," +
                            "CHECK (imageSetId IS NOT NULL OR videoSetId IS NOT NULL OR fileSetId IS NOT NULL OR quizId IS NOT NULL)" +
                            ");"
            );
            database.execSQL("DROP TABLE IF EXISTS post;");
            database.execSQL("ALTER TABLE post_new RENAME TO post;");

            //questionOption
            database.execSQL(
                    "CREATE TABLE questionOption_new (" +
                            "questionId TEXT NOT NULL, " +
                            "choice TEXT NOT NULL, " +
                            "isCorrect INTEGER NOT NULL DEFAULT 0, " +
                            "PRIMARY KEY(questionId, choice), " +
                            "FOREIGN KEY(questionId) REFERENCES quizQuestion(questionId) ON DELETE CASCADE" +
                            ");"
            );

            database.execSQL("DROP TABLE IF EXISTS questionOption;");
            database.execSQL("ALTER TABLE questionOption_new RENAME TO questionOption;");

            //quiz
            database.execSQL(
                    "CREATE TABLE quiz_new (" +
                            "quizId TEXT PRIMARY KEY " +
                            ");"
            );
            database.execSQL("DROP TABLE IF EXISTS quiz;");
            database.execSQL("ALTER TABLE quiz_new RENAME TO quiz;");

            //quizHistory
            database.execSQL(
                    "CREATE TABLE quizHistory_new (" +
                            "quizId TEXT PRIMARY KEY, " +
                            "userId TEXT NOT NULL, " +
                            "score INTEGER NOT NULL, " +
                            "timestamp TEXT NOT NULL, " +
                            "FOREIGN KEY(quizId) REFERENCES quiz(quizId) ON DELETE CASCADE, " +
                            "FOREIGN KEY(userId) REFERENCES user(userId) ON DELETE CASCADE" +
                            ");"
            );
            database.execSQL("DROP TABLE IF EXISTS quizHistory;");
            database.execSQL("ALTER TABLE quizHistory_new RENAME TO quizHistory;");

            //quizOld
            database.execSQL(
                    "CREATE TABLE quizOld_new (" +
                            "quizId TEXT NOT NULL, " +
                            "postId TEXT NOT NULL, " +
                            "PRIMARY KEY(quizId, postId), " +
                            "FOREIGN KEY(quizId) REFERENCES quiz(quizId) ON DELETE CASCADE, " +
                            "FOREIGN KEY(postId) REFERENCES post(postId) ON DELETE CASCADE" +
                            ");"
            );
            database.execSQL("DROP TABLE IF EXISTS quizOld;");
            database.execSQL("ALTER TABLE quizOld_new RENAME TO quizOld;");

            //quizQuestion
            database.execSQL(
                    "CREATE TABLE IF NOT EXISTS quizQuestion_new (" +
                            "questionId TEXT PRIMARY KEY, " +
                            "quizId TEXT NOT NULL, " +
                            "question TEXT NOT NULL, " +
                            "questionNo INTEGER NOT NULL," +
                            "FOREIGN KEY(quizId) REFERENCES quiz(quizId) ON DELETE CASCADE " +
                            ");"
            );
            database.execSQL("DROP TABLE IF EXISTS quizQuestion;");
            database.execSQL("ALTER TABLE quizQuestion_new RENAME TO quizQestion;");

            //quizResult
            database.execSQL(
                    "CREATE TABLE IF NOT EXISTS quizResult_new (" +
                            "quizResultId TEXT PRIMARY KEY, " +
                            "questionId TEXT NOT NULL, " +
                            "userId TEXT NOT NULL, " +
                            "userAns TEXT NOT NULL, " +
                            "timestamp TEXT NOT NULL, " +
                            "FOREIGN KEY(questionId) REFERENCES quizQuestion(questionId) ON DELETE CASCADE, " +
                            "FOREIGN KEY(userId) REFERENCES user(userId) ON DELETE CASCADE, " +
                            "FOREIGN KEY(userAns) REFERENCES questionOption(choice) ON DELETE CASCADE" +
                            ");"
            );

            database.execSQL("DROP TABLE IF EXISTS quizResult;");
            database.execSQL("ALTER TABLE quizResult_new RENAME TO quizResult;");

            //staff
            database.execSQL(
                    "CREATE TABLE IF NOT EXISTS staff_new (" +
                            "staffId TEXT PRIMARY KEY, " +
                            "name TEXT NOT NULL UNIQUE, " +
                            "email TEXT NOT NULL UNIQUE, " +
                            "password TEXT NOT NULL " +
                            ");"
            );

            database.execSQL("DROP TABLE IF EXISTS staff;");

            database.execSQL("ALTER TABLE staff_new RENAME TO staff;");

            //timeslot
            database.execSQL(
                    "CREATE TABLE IF NOT EXISTS timeslot_new (" +
                            "timeslotId TEXT PRIMARY KEY, " +
                            "startTime INTEGER NOT NULL, " +
                            "endTime INTEGER NOT NULL " +
                            ");"
            );
            database.execSQL("DROP TABLE IF EXISTS timeslot;");
            database.execSQL("ALTER TABLE timeslot_new RENAME TO timeslot;");

            // user
            database.execSQL("CREATE TABLE user_new (" +
                    "userId TEXT PRIMARY KEY, " +
                    "name TEXT NOT NULL DEFAULT 'bookworm', " +
                    "email TEXT NOT NULL UNIQUE, " +
                    "phoneNo TEXT UNIQUE, " +
                    "password TEXT NOT NULL, " +
                    "profilePic TEXT NOT NULL DEFAULT 'default_profile_pic_url', " +
                    "lastLogin TEXT NOT NULL, " +
                    "strikeLoginDays INTEGER NOT NULL);");

            // Drop the old tables (assuming there is no data migration needed)
            database.execSQL("DROP TABLE IF EXISTS user;");

            // Rename new tables to the original table names
            database.execSQL("ALTER TABLE user_new RENAME TO user;");

            //check if the username is unique before inserting or updating the username other than "bookworm"
            database.execSQL(
                    "CREATE TRIGGER check_unique_username " +
                            "BEFORE INSERT ON user " +
                            "WHEN NEW.name != 'bookworm' " + // Only check non-default usernames
                            "BEGIN " +
                            "    SELECT RAISE(FAIL, 'Username already exists') " +
                            "    WHERE (SELECT COUNT(*) FROM user WHERE name = NEW.name) > 0; " +
                            "END;"
            );

            // Add a trigger for updates
            database.execSQL(
                    "CREATE TRIGGER check_unique_username_update " +
                            "BEFORE UPDATE ON user " +
                            "WHEN NEW.name != 'bookworm' AND OLD.name != NEW.name " + // Only check when username changes
                            "BEGIN " +
                            "    SELECT RAISE(FAIL, 'Username already exists') " +
                            "    WHERE (SELECT COUNT(*) FROM user WHERE name = NEW.name) > 0; " +
                            "END;"
            );

            // Add a query to update streak counts for all users
            database.execSQL(
                    "UPDATE user " +
                            "SET " +
                            "    strikeLoginDays = CASE " +
                            "        WHEN lastLogin = date('now', '-1 day') THEN strikeLoginDays + 1 " +
                            "        ELSE 1 " +
                            "    END, " +
                            "    lastLogin = date('now')"
            );

            //userHistory
            database.execSQL(
                    "CREATE TABLE IF NOT EXISTS userHistory_new (" +
                            "postId TEXT NOT NULL, " +
                            "userId TEXT NOT NULL, " +
                            "progress INTEGER NOT NULL DEFAULT 1, " +
                            "time TEXT NOT NULL, " +
                            "PRIMARY KEY(postId, userId), " +
                            "FOREIGN KEY(postId) REFERENCES post(postId) ON DELETE CASCADE, " +
                            "FOREIGN KEY(userId) REFERENCES user(userId) ON DELETE CASCADE" +
                            ");"
            );
            database.execSQL("DROP TABLE IF EXISTS userHistory;");
            database.execSQL("ALTER TABLE userHistory_new RENAME TO userHistory;");

            //verEducator
            database.execSQL(
                    "CREATE TABLE IF NOT EXISTS verEducator_new (" +
                            "userId TEXT NOT NULL, " +
                            "imageSetId TEXT UNIQUE, " +
                            "fileSetId TEXT UNIQUE, " +
                            "domainId TEXT NOT NULL, " +
                            "staffId TEXT NOT NULL, " +
                            "verifiedStatus TEXT NOT NULL DEFAULT 'pending', " +
                            "PRIMARY KEY(userId, domainId), " +
                            "FOREIGN KEY(userId) REFERENCES user(userId) ON DELETE CASCADE, " +
                            "FOREIGN KEY(domainId) REFERENCES domain(domainId) ON DELETE CASCADE, " +
                            "FOREIGN KEY(staffId) REFERENCES staff(staffId) ON DELETE RESTRICT, " +
                            "FOREIGN KEY(imageSetId) REFERENCES mediaSet(mediaSetId) ON DELETE SET NULL, " +
                            "FOREIGN KEY(fileSetId) REFERENCES mediaSet(mediaSetId) ON DELETE SET NULL " +
                            ");"
            );
            database.execSQL("DROP TABLE IF EXISTS verEducator;");
            database.execSQL("ALTER TABLE verEducator_new RENAME TO verEducator;");

            //verPost
            database.execSQL(
                    "CREATE TABLE IF NOT EXISTS verPost_new (" +
                            "verPostId TEXT PRIMARY KEY, " +
                            "postId TEXT NOT NULL UNIQUE, " +
                            "staffId TEXT NOT NULL, " +
                            "verifiedStatus TEXT NOT NULL DEFAULT 'pending', " +
                            "FOREIGN KEY(postId) REFERENCES post(postId) ON DELETE CASCADE, " +
                            "FOREIGN KEY(staffId) REFERENCES staff(staffId) ON DELETE RESTRICT " +
                            ");"
            );

            database.execSQL("DROP TABLE IF EXISTS verPost;");

            database.execSQL("ALTER TABLE verPost_new RENAME TO verPost;");
        }
    };
}
