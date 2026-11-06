
package com.issuepilot.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record DuplicateCheckRequest(

        @NotBlank
        @Size(min = 3, max = 200)
        String title,

        @Size(max = 5000)
        String description,

        @NotNull
        Long projectId

) {}
