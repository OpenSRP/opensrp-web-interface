package org.opensrp.web.rest.controller;

import com.google.gson.Gson;
import org.opensrp.core.dto.BranchDTO;
import org.opensrp.core.service.BranchService;
import org.opensrp.core.service.mapper.BranchMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("rest/api/v1/branch")
public class BranchRestController {

    @Autowired
    private BranchMapper branchMapper;

    @Autowired
    private BranchService branchService;

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public ResponseEntity<String> saveBranch(@RequestBody BranchDTO branchDTO) {
        String msg = "";
        try {
             branchService.save(branchMapper.map(branchDTO));
             msg = "Successfully created the branch!!!";
         } catch (Exception e) {
            msg = "Something went wrong. Please contract with the admin";
        }
        return new ResponseEntity<>(new Gson().toJson(msg), HttpStatus.OK);
    }

    @RequestMapping(value = "/update", method = RequestMethod.PUT)
    public ResponseEntity<String> updateBranch(@RequestBody BranchDTO branchDTO) {

        System.out.println("BRANCH UPDATE");

        String msg = "";
        try {
            branchService.update(branchMapper.map(branchDTO));
            msg = "Successfully updated the branch!!!";
        } catch (Exception e) {
            msg = "Something went wrong. Please contract with the admin";
        }
        return new ResponseEntity<>(new Gson().toJson(msg), HttpStatus.OK);
    }

    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteBranch(@RequestBody BranchDTO branchDTO) {

        String msg = "";
        try {
            branchService.delete(branchMapper.map(branchDTO));
            msg = "Successfully created the branch!!!";
        } catch (Exception e) {
            msg = "Something went wrong. Please contract with the admin";
        }
        return new ResponseEntity<>(new Gson().toJson(msg), HttpStatus.OK);
    }
}
