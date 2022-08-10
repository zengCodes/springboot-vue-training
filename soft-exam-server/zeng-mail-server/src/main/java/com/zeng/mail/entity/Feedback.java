package com.zeng.mail.entity;

import lombok.Data;

import javax.persistence.Table;
import java.io.Serializable;


@Table(name="exam_feedback")
@Data
public class Feedback implements Serializable {
    private static final long serialVersionUID = 550979088670747783L;

    private String id;

    private String userId;

    private String username;

    private String email;

    private String nickname;

    private String content;

}
