package com.utp.backwebintegrado.service;

import com.utp.backwebintegrado.dto.request.PatientRegisterRequest;
import com.utp.backwebintegrado.dto.response.PatientRegisterResponse;

public interface PatientService {
    PatientRegisterResponse createPatient(PatientRegisterRequest request);
}
