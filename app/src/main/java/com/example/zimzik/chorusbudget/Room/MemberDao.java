package com.example.zimzik.chorusbudget.Room;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface MemberDao {
    @Insert
    void insertAll(Member member);

    @Delete
    void delete(Member member);

    @Query("Select * FROM member")
    List<Member> getAllMembers();
}
