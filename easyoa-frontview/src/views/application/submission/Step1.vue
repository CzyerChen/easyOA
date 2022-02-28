<template>
  <div>
    <a-form style="max-width: 500px; margin: 40px auto 0;">
      <a-form-item
        label="请假类别"
        :labelCol="{span: 5}"
        :wrapperCol="{span: 19}"
        :validateStatus="validateStatusLeaveaType"
        :help="helpLeaveType"
      >
        <a-select
          mode="default"
          notFoundContent="暂无类别"
          v-model="form.leaveType"
          style="width: 100%"
          @change="handleSelectChange"
          v-decorator="['leaveType',{rules: [{ required: true, message: '请选择申请类别' }]}]"
        >
          <a-select-option v-for="a in applyTypes" :key="a">{{a}}</a-select-option>
        </a-select>
        <p :title="'事假情况下，若有年假剩余，优先申请年假，慎重填写'">说明：事假情况下，若有年假剩余，优先申请年假，慎重填写</p>
      </a-form-item>
      <a-form-item
        label="请假时间"
        :labelCol="{span: 5}"
        :wrapperCol="{span: 19}"
        :validateStatus="validateStatusDateTimeSelected"
        :help="helpDateTimeSelected"
      >
        <a-date-picker
          :showTime="{ 
      hideDisabledOptions:true,
      format: 'HH:mm',
      defaultValue: moment('08:30', 'HH:mm'),
      hourStep:1 ,
      minuteStep:30}"
          :disabledTime="disabledDateTimeBegin"
          :disabled-date="disabledStartDate"
          :showToday="false"
          format="YYYY-MM-DD HH:mm"
          v-model="startValue"
          placeholder="开始时间"
          @openChange="handleStartOpenChange"
        ></a-date-picker>
        <a-date-picker
          :showTime="{ 
       hideDisabledOptions:true,
       defaultValue: moment('17:30', 'HH:mm'),
       format: 'HH:mm' ,
       hourStep:1 ,
       minuteStep:30}"
          :disabledTime="disabledDateTimeEnd"
          :disabled-date="disabledEndDate"
          :showToday="false"
          format="YYYY-MM-DD HH:mm"
          placeholder="结束时间"
          v-model="endValue"
          :open="endOpen"
          @openChange="handleEndOpenChange"
        ></a-date-picker>
        <p :title="'请假最小单位0.5天(上午/下午)'">说明：上午: 8:30-12:00 下午:13:00-17:30</p>
      </a-form-item>
      <a-form-item label="请假原因" :labelCol="{span: 5}" :wrapperCol="{span: 19}">
        <a-textarea
          :rows="4"
          placeholder="请填写请假原因(100字以内)"
          v-model="form.leaveReason"
          v-decorator="[
          'leaveReason',
          {rules: [
            { max: 100, message: '长度不能超过100个字符'}
          ]}]"
        ></a-textarea>
      </a-form-item>
      <a-form-item label="上传附件" :labelCol="{span: 5}" :wrapperCol="{span: 19}">
        <a-upload
          class="upload-area"
          :fileList="fileList"
          :remove="handleRemove"
          :disabled="fileList.length === 1"
          :beforeUpload="beforeUpload"
        >
          <a-button>
            <a-icon type="upload" />点击选择文件
          </a-button>
        </a-upload>
        <a-button
          @click="handleUpload"
          :disabled="fileList.length === 0"
          :loading="uploading"
        >{{uploading === null? '点击上传':(uploading === true?'上传中' : '上传完毕' )}}</a-button>
        <p :title="'婚假、产假、陪产假、病假需提交相关证明'">说明：婚假、产假、陪产假、病假需提交相关证明</p>
      </a-form-item>
      <a-form-item :wrapperCol="{span: 19, offset: 5}">
        <a-button type="primary" @click="nextStep">下一步</a-button>
      </a-form-item>
    </a-form>
  </div>
</template>

<script>
import db from "utils/localstorage";
import { mapState, mapMutations } from "vuex";
import moment from "moment";
import "moment/locale/zh-cn";
moment.locale("zh-cn");

export default {
  name: "Step1",
  data() {
    return {
      validateStatusDateTimeSelected: "",
      validateStatusLeaveaType: "",
      helpDateTimeSelected: "",
      helpLeaveType: "",
      userPosition: [],
      userDept: [],
      applyTypes: [],
      leaveType: "",
      leaveDateRange: {},
      loading: false,
      fileList: [],
      startValue: null,
      endValue: null,
      form: {
        leaveType: "",
        leaveTimeFrom: "",
        leaveTimeTo: "",
        userId: "",
        leaveReason: "",
        uploadFile: ""
      },
      uploading: null,
      importData: [],
      importResultVisible: false,
      endOpen: false
    };
  },
  methods: {
    moment,
    handleSelectChange(value) {
      this.validateStatusLeaveaType = "success";
      this.helpLeaveType = "";
    },
    range(start, end) {
      const result = [];
      for (let i = start; i < end; i++) {
        result.push(i);
      }
      return result;
    },
     disabledStartDate(startValue) {
      const endValue = this.endValue;
      if (!startValue || !endValue) {
        return false;
      }
      return startValue.valueOf() > endValue.valueOf();
    },
    disabledEndDate(endValue) {
      const startValue = this.startValue;
      if (!endValue || !startValue) {
        return false;
      }
      return startValue.valueOf() >= endValue.valueOf();
    },
    disabledDateTimeBegin() {
      return {
        disabledHours: () => [
          0,
          1,
          2,
          3,
          4,
          5,
          6,
          7,
          9,
          10,
          11,
          12,
          14,
          15,
          16,
          17,
          18,
          19,
          20,
          21,
          22,
          23
        ],
        disabledMinutes: () => []
      };
    },
    disabledDateTimeEnd() {
      return {
        disabledHours: () => [
          0,
          1,
          2,
          3,
          4,
          5,
          6,
          7,
          8,
          9,
          10,
          11,
          13,
          14,
          15,
          16,
          18,
          19,
          20,
          21,
          22,
          23
        ]
      };
    },
    handleStartOpenChange(open) {
      if (this.endValue) {
        this.validateStatusDateTimeSelected = "success";
      }

      if (!open) {
        this.endOpen = true;
      }
    },
    handleEndOpenChange(open) {
      this.endOpen = open;
      if (this.startValue) {
        this.validateStatusDateTimeSelected = "success";
      }
    },
    reset() {
      this.form.leaveReason = "";
      this.form.leaveTimeFrom = "";
      this.form.leaveTimeTo = "";
      this.form.userId = "";
      this.form.leaveReason = "";
      this.uploading = null;
      this.startValue = null;
      this.endValue = null;
      this.validateStatusDateTimeSelected = "";
      this.validateStatusLeaveaType = "";
      this.helpDateTimeSelected = "";
      this.helpLeaveType = "";
    },
    nextStep() {
      if (
        this.validateStatusLeaveaType === "success" &&
        this.validateStatusDateTimeSelected === "success"
      ) {
        if(this.startValue)
        this.form.userId = this.currentUser.userId;
        this.form.leaveTimeFrom = this.startValue.format("YYYY-MM-DD HH:mm");
        this.form.leaveTimeTo = this.endValue.format("YYYY-MM-DD HH:mm");
        localStorage.setItem("APPLY_INFO", JSON.stringify(this.form));
        this.$emit("nextStep");
      } else {
        this.handleFormParams();
        this.handleDateTimeSelected();
      }
    },
    handleFormParams() {
      if (this.form.leaveType) {
        this.validateStatusLeaveaType = "success";
        this.helpLeaveType = "";
      } else {
        this.validateStatusLeaveaType = "error";
        this.helpLeaveType = "休假类型不能为空";
      }
    },
    handleDateTimeSelected() {
      if (this.startValue) {
        let start = this.startValue.format("HH:mm");
        if (start === "09:30" || start === "13:30") {
          this.validateStatusDateTimeSelected = "error";
          this.helpDateTimeSelected = "开始时间选择错误";
        } else {
          this.validateStatusDateTimeSelected = "success";
          this.helpDateTimeSelected = "";
        }
      } else {
        this.validateStatusDateTimeSelected = "error";
        this.helpDateTimeSelected = "时间选择不能为空";
      }

      if (this.endValue) {
        let end = this.endValue.format("HH:mm");
        if (end === "12:30" || end === "18:30") {
          this.validateStatusDateTimeSelected = "error";
          this.helpDateTimeSelected = "结束时间选择错误";
        } else {
          this.validateStatusDateTimeSelected = "success";
          this.helpDateTimeSelected = "";
        }
      } else {
        this.validateStatusDateTimeSelected = "error";
        this.helpDateTimeSelected = "时间选择不能为空";
      }
    },
    handleDateChange(value) {
      if (value) {
        this.form.leaveTimeFrom = value[0];
        this.form.leaveTimeTo = value[1];
      }
    },
    onDateChange(value, dateString) {
      this.form.leaveTimeFrom = dateString[0];
      this.form.leaveTimeTo = dateString[1];
    },
    onOk(value) {},
    handleClose() {
      this.importResultVisible = false;
      this.uploading = false;
      localStorage.removeItem("APPLY_INFO");
    },
    handleRemove(file) {
      if (this.uploading) {
        this.$message.warning("文件导入中，请勿删除");
        return;
      }
      const index = this.fileList.indexOf(file);
      const newFileList = this.fileList.slice();
      newFileList.splice(index, 1);
      this.fileList = newFileList;
    },
    beforeUpload(file) {
      this.fileList = [...this.fileList, file];
      return false;
    },
    handleUpload() {
      const { fileList } = this;
      const formData = new FormData();
      formData.append("file", fileList[0]);
      formData.append("userId", this.currentUser.userId);
      this.form.userId = this.currentUser.userId;
      this.uploading = true;
      this.$upload("apply/file", formData)
        .then(r => {
          let data = r.data;
          if (data.code === 200) {
            this.$message.success("文件上传成功");
            this.importData = data.data;
            this.errors = data.error;
            this.times = data.time / 1000;
            this.uploading = false;
            this.importResultVisible = true;
            this.form.uploadFile = data.data;
          }
        })
        .catch(r => {
          console.error(r);
          this.uploading = false;
          this.fileList = [];
        });
    },
    ...mapMutations({
      setUser: "account/setUser"
    }),
    handleUploadChange(info) {
      let fileList = [...info.fileList];
      fileList = fileList.slice(-2);
      fileList = fileList.map(file => {
        if (file.response) {
          file.url = file.response.url;
        }
        return file;
      });
      this.fileList = fileList;
    }
  },
  mounted() {
    this.$get(`apply/types/${this.currentUser.userId}`).then(r => {
      this.applyTypes = r.data;
    });
    this.reset();
    if (localStorage.getItem("APPLY_INFO") != null) {
      this.form = JSON.parse(localStorage.getItem("APPLY_INFO"));
    }
  },
  computed: {
    ...mapState({
      currentUser: state => state.account.user
    })
  },
  watch: {}
};
</script>

<style scoped>
</style>
