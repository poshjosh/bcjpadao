/*
 * Copyright 2017 NUROX Ltd.
 *
 * Licensed under the NUROX Ltd Software License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.looseboxes.com/legal/licenses/software.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bc.tasktracker.jpa.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * @author Chinomso Bassey Ikwuagwu on Mar 10, 2017 2:49:26 PM
 */
@Entity
@Table(name = "doc")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Doc.findAll", query = "SELECT d FROM Doc d"),
    @NamedQuery(name = "Doc.findByDocid", query = "SELECT d FROM Doc d WHERE d.docid = :docid"),
    @NamedQuery(name = "Doc.findByDatesigned", query = "SELECT d FROM Doc d WHERE d.datesigned = :datesigned"),
    @NamedQuery(name = "Doc.findByReferencenumber", query = "SELECT d FROM Doc d WHERE d.referencenumber = :referencenumber"),
    @NamedQuery(name = "Doc.findBySubject", query = "SELECT d FROM Doc d WHERE d.subject = :subject"),
    @NamedQuery(name = "Doc.findByTimecreated", query = "SELECT d FROM Doc d WHERE d.timecreated = :timecreated"),
    @NamedQuery(name = "Doc.findByTimemodified", query = "SELECT d FROM Doc d WHERE d.timemodified = :timemodified")})
public class Doc implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "docid")
    private Integer docid;
    @Column(name = "datesigned")
    @Temporal(TemporalType.DATE)
    private Date datesigned;
    @Column(name = "referencenumber")
    private String referencenumber;
    @Basic(optional = false)
    @Column(name = "subject")
    private String subject;
    @Basic(optional = false)
    @Column(name = "timecreated")
    @Temporal(TemporalType.TIMESTAMP)
    private Date timecreated;
    @Basic(optional = false)
    @Column(name = "timemodified")
    @Temporal(TemporalType.TIMESTAMP)
    private Date timemodified;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "doc", fetch = FetchType.LAZY)
    private List<Task> taskList;

    public Doc() {
    }

    public Doc(Integer docid) {
        this.docid = docid;
    }

    public Doc(Integer docid, String subject, Date timecreated, Date timemodified) {
        this.docid = docid;
        this.subject = subject;
        this.timecreated = timecreated;
        this.timemodified = timemodified;
    }

    public Integer getDocid() {
        return docid;
    }

    public void setDocid(Integer docid) {
        this.docid = docid;
    }

    public Date getDatesigned() {
        return datesigned;
    }

    public void setDatesigned(Date datesigned) {
        this.datesigned = datesigned;
    }

    public String getReferencenumber() {
        return referencenumber;
    }

    public void setReferencenumber(String referencenumber) {
        this.referencenumber = referencenumber;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public Date getTimecreated() {
        return timecreated;
    }

    public void setTimecreated(Date timecreated) {
        this.timecreated = timecreated;
    }

    public Date getTimemodified() {
        return timemodified;
    }

    public void setTimemodified(Date timemodified) {
        this.timemodified = timemodified;
    }

    @XmlTransient
    public List<Task> getTaskList() {
        return taskList;
    }

    public void setTaskList(List<Task> taskList) {
        this.taskList = taskList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (docid != null ? docid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Doc)) {
            return false;
        }
        Doc other = (Doc) object;
        if ((this.docid == null && other.docid != null) || (this.docid != null && !this.docid.equals(other.docid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return this.getClass().getName() + "[ docid=" + docid + " ]";
    }

}
