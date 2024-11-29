package com.example.madguardians.database;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FirestoreManager {
    private final FirebaseFirestore firestore;
    private final AppDatabase database;

    public FirestoreManager(AppDatabase database) {
        this.firestore = FirebaseFirestore.getInstance();
        this.database = database;
    }

    public void clearTables(){
        database.userDao().deleteAll();
        database.domainDao().deleteAll();
        database.domainInterestedDao().deleteAll();
        database.verEducatorDao().deleteAll();
        database.quizHistoryDao().deleteAll();
        database.quizResultDao().deleteAll();
        database.collectionDao().deleteAll();
        database.folderDao().deleteAll();
        database.userHistoryDao().deleteAll();
        database.mediaReadDao().deleteAll();
        database.appointmentDao().deleteAll();
        database.achievementDao().deleteAll();
        database.chatHistoryDao().deleteAll();
        database.notificationDao().deleteAll();
        database.verPostDao().deleteAll();
        database.mediaSetDao().deleteAll();
        database.questionOptionDao().deleteAll();
        database.quizQuestionDao().deleteAll();
        database.quizOldDao().deleteAll();
        database.postDao().deleteAll();
        database.courseDao().deleteAll();
        database.commentDao().deleteAll();
        database.issueDao().deleteAll();
        database.timeslotDao().deleteAll();
        database.counselorAvailabilityDao().deleteAll();
        database.badgeDao().deleteAll();
        database.helpdeskDao().deleteAll();
        database.counselorDao().deleteAll();
        database.staffDao().deleteAll();
    }

    public void onLoginSyncUser(String userId){
        clearTables();
        syncUserPartial(userId);
        syncUserFull();
    }

    public void onLoginSyncStaff(){
        clearTables();
        syncUserFull();
        LiveData<List<Post>> allPostsLiveData =
                syncAll("post", Post.class);
        LiveData<List<User>> allUserLiveData =
                syncAll("user", User.class);
        LiveData<List<DomainInterested>> allDomainInterestedLiveData =
                syncAll("domainInterested", DomainInterested.class);
        LiveData<List<VerEducator>> allVerEducatorLiveData =
                syncAll("verEducator", VerEducator.class);
        LiveData<List<QuizHistory>> allQuizHistoryLiveData =
                syncAll("quizHistory", QuizHistory.class);
        LiveData<List<QuizResult>> allQuizResultLiveData =
                syncAll("quizResult", QuizResult.class);
        LiveData<List<Collection>> allCollectionLiveData =
                syncAll("collection", Collection.class);
        LiveData<List<Folder>> allFolderLiveData =
                syncAll("folder", Folder.class);
        LiveData<List<UserHistory>> allUserHistoryLiveData =
                syncAll("userHistory", UserHistory.class);
        LiveData<List<MediaRead>> allMediaReadLiveData =
                syncAll("mediaRead", MediaRead.class);
        LiveData<List<Appointment>> allAppointmentLiveData =
                syncAll("appointment", Appointment.class);
        LiveData<List<Achievement>> allAchievementLiveData =
                syncAll("achievement", Achievement.class);
        LiveData<List<ChatHistory>> allChatHistoryLiveData =
                syncAll("chatHistory", ChatHistory.class);
        LiveData<List<Notification>> allNotificationLiveData =
                syncAll("notification", Notification.class);
        LiveData<List<VerPost>> allVerPostLiveData =
                syncAll("verPost", VerPost.class);
        LiveData<List<MediaSet>> allMediaSetLiveData =
                syncAll("mediaSet", MediaSet.class);
    }

    public void onLoginSyncCounselor(String counselorId){
        clearTables();
        syncPartialRoleId(counselorId, "counselorId", "appointment");
        syncPartialRoleId(counselorId, "counselorId", "counselorAvailability");
        syncPartialRoleId(counselorId, "counselorId", "counselor");
        syncPartialRoleId(counselorId, "counselorId", "chatHistory");
        syncPartialJoinByCounselorAvailability(counselorId,  appointments->{
            List<Appointment> appointmentss= (List<Appointment>)appointments;
            if (appointmentss != null) {
                database.appointmentDao().insertAll(appointmentss);
            } else {
                // Handle error
                Log.e("Firestore", "Error fetching media set");
            }
        });
        LiveData<List<Timeslot>> allTimeslotLiveData =
                syncAll("timeslot", Timeslot.class);
    }

    public void onInsertUpdate(String tableName, String documentId, Object curentChange){

    }

    // Fetch current user data (upon the login)
    // if only register then no need to fetch the user data
    public void syncUserPartial(String userId){
        syncPartialDocumentId(userId,  "user");
        syncPartialRoleId(userId, "userId", "domainInterested");
        syncPartialRoleId(userId, "userId", "verEducator");
        syncPartialRoleId(userId, "userId", "quizHistory");
        syncPartialRoleId(userId, "userId", "quizResult");
        syncPartialRoleId(userId, "userId", "collection");
        syncPartialRoleId(userId, "userId", "folder");
        syncPartialRoleId(userId, "userId", "userHistory");
        syncPartialRoleId(userId, "userId", "mediaRead");
        syncPartialRoleId(userId, "userId", "appointment");
        syncPartialRoleId(userId, "userId", "achievement");
        syncPartialRoleId(userId, "senderUserId", "chatHistory");
        syncPartialRoleId(userId, "recipientUserId", "chatHistory");
        syncPartialRoleId(userId, "userId", "notification");
        syncPartialJoinByPost(userId, "verPost",  verPosts->{
            List<VerPost> verPostss= (List<VerPost>)verPosts;
            if (verPostss != null) {
                database.verPostDao().insertAll(verPostss);
            } else {
                // Handle error
                Log.e("Firestore", "Error fetching verification posts");
            }
        });
        syncPartialJoinByPost(userId, "mediaSet",  mediaSets->{
            List<MediaSet> mediaSetss= (List<MediaSet>)mediaSets;
            if (mediaSetss != null) {
                database.mediaSetDao().insertAll(mediaSetss);
            } else {
                // Handle error
                Log.e("Firestore", "Error fetching media set");
            }
        });
    }

    //Fetch full access data into Room
    public void syncUserFull(){
        LiveData<List<Domain>> allDomainLiveData =
                syncAll("domain", Domain.class);
        LiveData<List<QuestionOption>> allQuestionOptionLiveData =
                syncAll("questionOption", QuestionOption.class);
        LiveData<List<QuizQuestion>> allQuizQuestionLiveData =
                syncAll("quizQuestion", QuizQuestion.class);
        LiveData<List<QuizOld>> allQuizOldLiveData =
                syncAll("quizOld", QuizOld.class);
        LiveData<List<Post>> allPostLiveData =
                syncAll("post", Post.class);
        LiveData<List<Course>> allCourseLiveData =
                syncAll("course", Course.class);
        LiveData<List<Comment>> allCommentLiveData =
                syncAll("comment", Comment.class);
        LiveData<List<Issue>> allIssueLiveData =
                syncAll("issue", Issue.class);
        LiveData<List<Timeslot>> allTimeslotLiveData =
                syncAll("timeslot", Timeslot.class);
        LiveData<List<CounselorAvailability>> allCounselorAvailabilityLiveData =
                syncAll("counselorAvailability", CounselorAvailability.class);
        LiveData<List<Badge>> allBadgeLiveData =
                syncAll("badge", Badge.class);
    }

    public void syncPartialJoinByCounselorAvailability(String counselorId, OnCompleteListener<List<Appointment>> callback) {
        fetchAvailability(counselorId, availabilitys->{
            List<Task<QuerySnapshot>> tasks = new ArrayList<>();
            int batchSize = 10;
            for (int i = 0; i < ((List<String>)availabilitys).size(); i += batchSize) {
                List<String> batch = ((List<String>)availabilitys).subList(i, Math.min(((List<String>)availabilitys).size(), i + batchSize));
                tasks.add(firestore.collection("appointment").whereIn("counselorAvailabilityId", batch).get());
            }

            Tasks.whenAllComplete(tasks).addOnCompleteListener(allTasks -> {
                List<Appointment> appointments = new ArrayList<>();
                for (Task<?> task : tasks) {
                    if (task.isSuccessful()) {
                        QuerySnapshot snapshot = (QuerySnapshot) task.getResult();
                        for (DocumentSnapshot doc : snapshot.getDocuments()) {
                            appointments.add(doc.toObject(Appointment.class));
                        }
                    }
                }
                callback.onComplete(Tasks.forResult(appointments));
            });
        });
    }

    private void fetchAvailability(String counselorId, OnCompleteListener<List<String>> callback) {
        firestore.collection("courseAvailability")
                .whereEqualTo("counselorId", counselorId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<String> counselorAvailabilityIds = new ArrayList<>();
                        for (DocumentSnapshot document : task.getResult().getDocuments()) {
                            counselorAvailabilityIds.add(document.getId());
                        }
                        callback.onComplete(Tasks.forResult(counselorAvailabilityIds));
                    } else {
                        Log.e("FirestoreSync", "Failed to fetch counselor availability for counselorId: " + counselorId);
                        callback.onComplete(Tasks.forResult(Collections.emptyList()));
                    }
                });
    }

    //identifier = the id of counselor/user
    //compareWith = column in firestore to compare
    //tableName = which table you are referring
    public void syncPartialDocumentId(String identifier,  String tableName) {
        firestore.collection(tableName)
                .document(identifier)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Object currentObject = documentSnapshot.toObject(getDomainClass(tableName));
                        if (currentObject != null) {
                            Executor.executeTask(() -> insertDataToDatabase(tableName, currentObject));
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e("FirestoreSync", "Failed to sync data for table: " + tableName, e));
    }

    public void syncPartialRoleId(String identifier, String compareWith, String tableName){
        firestore.collection(tableName)
                .whereEqualTo(compareWith, identifier)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<?> resultList = querySnapshot.toObjects(getDomainClass(tableName));
                    Executor.executeTask(() -> {
                        insertDataToDatabase(tableName, resultList);
                    });
                })
                .addOnFailureListener(e -> Log.e("FirestoreSync", "Failed to sync data for table: " + tableName, e));
    }

    private Class<?> getDomainClass(String tableName) {
        switch (tableName) {
            case "user":
                return User.class;
            case "counselor":
                return Counselor.class;
            case "domainInterested":
                return DomainInterested.class;
            case "verEducator":
                return VerEducator.class;
            case "quizHistory":
                return QuizHistory.class;
            case "quizResult":
                return QuizResult.class;
            case "collection":
                return Collection.class;
            case "folder":
                return Folder.class;
            case "userHistory":
                return UserHistory.class;
            case "mediaRead":
                return MediaRead.class;
            case "appointment":
                return Appointment.class;
            case "achievement":
                return Achievement.class;
            case "chatHistory":
                return ChatHistory.class;
            case "notification":
                return Notification.class;
            case "mediaSet":
                return MediaSet.class;
            default:
                return null; // Handle default case if needed
        }
    }

    private void insertDataToDatabase(String tableName, Object currentObject) {
        switch (tableName){
            case "user":
                database.userDao().insert((User)currentObject);
                break;
            case "counselor":
                database.counselorDao().insert((Counselor)currentObject);
                break;
        }
    }
    private void insertDataToDatabase(String tableName, List<?> data) {
        switch (tableName) {
            case "domainInterested":
                database.domainInterestedDao().insertAll((List<DomainInterested>) data);
                break;
            case "verEducator":
                database.verEducatorDao().insertAll((List<VerEducator>) data);
                break;
            case "quizHistory":
                database.quizHistoryDao().insertAll((List<QuizHistory>) data);
                break;
            case "quizResult":
                database.quizResultDao().insertAll((List<QuizResult>) data);
                break;
            case "collection":
                database.collectionDao().insertAll((List<Collection>) data);
                break;
            case "folder":
                database.folderDao().insertAll((List<Folder>) data);
                break;
            case "userHistory":
                database.userHistoryDao().insertAll((List<UserHistory>) data);
                break;
            case "mediaRead":
                database.mediaReadDao().insertAll((List<MediaRead>) data);
                break;
            case "appointment":
                database.appointmentDao().insertAll((List<Appointment>) data);
                break;
            case "achievement":
                database.achievementDao().insertAll((List<Achievement>) data);
                break;
            case "chatHistory":
                database.chatHistoryDao().insertAll((List<ChatHistory>) data);
                break;
            case "notification":
                database.notificationDao().insertAll((List<Notification>) data);
                break;
            case "mediaSet":
                database.mediaSetDao().insertAll((List<MediaSet>) data);
                break;
            default:
                Log.e("FirestoreSync", "Unknown table name: " + tableName);
                break;
        }
    }

    public void syncPartialJoinByPost(String userId, String tableName, OnCompleteListener<List<?>> callback) {

        // Step 1: Fetch posts by userId to get the list of IDs for the join
        fetchUserPosts(userId, postIds -> {
            if (!((List<String>)postIds).isEmpty()) {
                // Step 2: Depending on the tableName, fetch the corresponding data (VerPost, MediaSet, etc.)
                switch (tableName) {
                    case "verPost":
                        // Fetch VerPosts by postIds
                        getVerPostsByPostIds((List<String>) postIds, callback);
                        break;

                    case "mediaSet":
                        // Extract MediaSet IDs from the posts and fetch MediaSets
                        List<String> mediaSetIds = extractMediaSetIdsFromPosts((List<String>) postIds);
                        if (!mediaSetIds.isEmpty()) {
                            getMediaSetsByIds(mediaSetIds, callback);
                        }
                        break;

                    // Add more cases for other tables as needed, e.g., "userHistory", "chatHistory", etc.
                    default:
                        Log.e("FirestoreSync", "Unsupported table: " + tableName);
                        callback.onComplete(Tasks.forResult(Collections.emptyList()));
                        break;
                }
            } else {
                // If no posts exist for the user, return an empty list
                callback.onComplete(Tasks.forResult(Collections.emptyList()));
            }
        });
    }

    private void fetchUserPosts(String userId, OnCompleteListener<List<String>> callback) {
        firestore.collection("post")
                .whereEqualTo("userId", userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<String> postIds = new ArrayList<>();
                        for (DocumentSnapshot document : task.getResult().getDocuments()) {
                            postIds.add(document.getId()); // Collect post IDs
                        }
                        callback.onComplete(Tasks.forResult( postIds));
                    } else {
                        Log.e("FirestoreSync", "Failed to fetch posts for userId: " + userId);
                        callback.onComplete(Tasks.forResult(Collections.emptyList()));
                    }
                });
    }

    private List<String> extractMediaSetIdsFromPosts(List<String> postIds) {
        List<String> mediaSetIds = new ArrayList<>();

        // Iterate over post IDs and fetch relevant MediaSetIds (imageSetId, videoSetId, fileSetId)
        for (String postId : postIds) {
            firestore.collection("post").document(postId).get()
                    .addOnSuccessListener(document -> {
                        String imageSetId = document.getString("imageSetId");
                        String videoSetId = document.getString("videoSetId");
                        String fileSetId = document.getString("fileSetId");

                        if (imageSetId != null) mediaSetIds.add(imageSetId);
                        if (videoSetId != null) mediaSetIds.add(videoSetId);
                        if (fileSetId != null) mediaSetIds.add(fileSetId);
                    })
                    .addOnFailureListener(e -> Log.e("FirestoreSync", "Failed to extract MediaSetIds from post"));
        }

        return mediaSetIds;
    }

    private void getVerPostsByPostIds(List<String> postIds, OnCompleteListener<List<?>> callback) {
        List<Task<QuerySnapshot>> tasks = new ArrayList<>();

        // Divide postIds into batches of 10
        int batchSize = 10;
        for (int i = 0; i < postIds.size(); i += batchSize) {
            List<String> batch = postIds.subList(i, Math.min(postIds.size(), i + batchSize));
            Task<QuerySnapshot> task = firestore.collection("verPost")
                    .whereIn("postId", batch)
                    .get();
            tasks.add(task);
        }

        // Combine all tasks into one
        Tasks.whenAllComplete(tasks).addOnCompleteListener(allTasks -> {
            List<VerPost> allVerPosts = new ArrayList<>();
            for (Task<?> task : tasks) {
                if (task.isSuccessful() && task.getResult() instanceof QuerySnapshot) {
                    QuerySnapshot snapshot = (QuerySnapshot) task.getResult();
                    for (DocumentSnapshot doc : snapshot.getDocuments()) {
                        allVerPosts.add(doc.toObject(VerPost.class));
                    }
                }
            }
            callback.onComplete(Tasks.forResult(allVerPosts)); // Pass the combined results to the callback
        });
    }

    private void getMediaSetsByIds(List<String> mediaSetIds, OnCompleteListener<List<?>> callback) {
        List<Task<QuerySnapshot>> tasks = new ArrayList<>();

        // Perform batched queries (limit the batch size to avoid overloading)
        int batchSize = 10;  // Adjust batch size as needed
        for (int i = 0; i < mediaSetIds.size(); i += batchSize) {
            List<String> batch = mediaSetIds.subList(i, Math.min(mediaSetIds.size(), i + batchSize));
            Task<QuerySnapshot> task = firestore.collection("mediaSet")
                    .whereIn("mediaSetId", batch)
                    .get();
            tasks.add(task);
        }

        // Handle all queries once completed
        Tasks.whenAllComplete(tasks).addOnCompleteListener(allTasks -> {
            List<MediaSet> mediaSets = new ArrayList<>();
            for (Task<?> task : tasks) {
                if (task.isSuccessful()) {
                    QuerySnapshot snapshot = (QuerySnapshot) task.getResult();
                    for (DocumentSnapshot doc : snapshot.getDocuments()) {
                        mediaSets.add(doc.toObject(MediaSet.class));  // Add the MediaSet object to the list
                    }
                } else {
                    Log.e("FirestoreSync", "Error fetching media sets");
                }
            }

            callback.onComplete(Tasks.forResult(mediaSets));  // Pass the combined results to the callback
        });
    }

    public <T> LiveData<List<T>> syncAll(String collectionName, Class<T> modelClass) {
        MutableLiveData<List<T>> liveData = new MutableLiveData<>();

        firestore.collection(collectionName)
                .addSnapshotListener((querySnapshot, e) -> {
                    if (e != null) {
                        Log.e("FirestoreSync", "Listen failed.", e);
                        return;
                    }

                    if (querySnapshot != null) {
                        List<T> dataList = querySnapshot.toObjects(modelClass);
                        liveData.setValue(dataList); // Update LiveData for UI

                        // Update local database for offline access
                        Executor.executeTask(() -> {
                            if (modelClass == Post.class) {
                                database.postDao().insertAll((List<Post>) dataList);
                            } else if (modelClass == User.class) {
                                database.userDao().insertAll((List<User>) dataList);
                            } else if (modelClass == DomainInterested.class) {
                                database.domainInterestedDao().insertAll((List<DomainInterested>) dataList);
                            }
                        });
                    }
                });

        return liveData;
    }

}