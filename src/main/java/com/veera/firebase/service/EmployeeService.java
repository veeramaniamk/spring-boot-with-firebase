package com.veera.firebase.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import com.veera.firebase.model.Employee;
import org.springframework.stereotype.Service;

@Service
public class EmployeeService {

    private static final String COLLECTION = "employees";

    public String save(Employee employee) throws Exception {

        Firestore firestore = FirestoreClient.getFirestore();

        ApiFuture<WriteResult> future =
                firestore.collection(COLLECTION)
                        .document(employee.getId())
                        .set(employee);

        return future.get().getUpdateTime().toString();
    }

    public Employee getEmployee(String id) throws Exception {

        Firestore firestore = FirestoreClient.getFirestore();

        DocumentReference ref =
                firestore.collection(COLLECTION).document(id);

        DocumentSnapshot snapshot = ref.get().get();

        if (snapshot.exists()) {
            return snapshot.toObject(Employee.class);
        }

        return null;
    }

    public String update(Employee employee) throws Exception {

        Firestore firestore = FirestoreClient.getFirestore();

        ApiFuture<WriteResult> future =
                firestore.collection(COLLECTION)
                        .document(employee.getId())
                        .set(employee);

        return future.get().getUpdateTime().toString();
    }

    public String delete(String id) throws Exception {

        Firestore firestore = FirestoreClient.getFirestore();

        ApiFuture<WriteResult> future =
                firestore.collection(COLLECTION)
                        .document(id)
                        .delete();

        return future.get().getUpdateTime().toString();
    }

}