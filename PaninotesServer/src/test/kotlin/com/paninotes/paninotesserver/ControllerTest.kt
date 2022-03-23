package com.paninotes.paninotesserver

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.web.servlet.MockMvc


@WebMvcTest
@JsonIgnoreProperties
internal class ControllerTest(@Autowired val mockMvc: MockMvc)