import { Button, Form, Input } from "antd";
import { FormComponentProps } from "antd/lib/form";
import React from "react";

class AccountForm extends React.Component<FormComponentProps & {
    account: Account
}> {

  handleSubmit = (e: React.FormEvent<any>) => {
    e.preventDefault();
    this.props.form.validateFieldsAndScroll((err, values) => {
      if (!err) {
        
      }
    });
  };

  render() {
    const { account } = this.props;
    const { getFieldDecorator } = this.props.form;

    const formItemLayout = {
      labelCol: {
        xs: { span: 24 }
      },
      wrapperCol: {
        xs: { span: 24 }
      }
    };
    const tailFormItemLayout = {
      wrapperCol: {
        xs: {
          span: 24,
          offset: 0
        }
      }
    };

    return (
      <Form onSubmit={this.handleSubmit}>
        <Form.Item {...formItemLayout} label="id">
          {getFieldDecorator("id", {
            initialValue: account.id,
            rules: []
          })(<Input />)}
        </Form.Item>
        <Form.Item {...formItemLayout} label="昵称">
          {getFieldDecorator("nickName", {
            initialValue: account.nickName,
            rules: []
          })(<Input />)}
        </Form.Item>
        <Form.Item {...formItemLayout} label="密码（hash）">
          {getFieldDecorator("password", {
            initialValue: account.password,
            rules: []
          })(<Input />)}
        </Form.Item>
        <Form.Item {...formItemLayout} label="邮箱">
          {getFieldDecorator("email", {
            initialValue: account.email,
            rules: []
          })(<Input />)}
        </Form.Item>
        <Form.Item {...formItemLayout} label="手机">
          {getFieldDecorator("phone", {
            initialValue: account.phone,
            rules: []
          })(<Input />)}
        </Form.Item>
        <Form.Item {...formItemLayout} label="头像">
          {getFieldDecorator("avatar", {
            initialValue: account.avatar,
            rules: []
          })(<Input />)}
        </Form.Item>
        <Form.Item {...formItemLayout} label="真实名">
          {getFieldDecorator("realName", {
            initialValue: account.realName,
            rules: []
          })(<Input />)}
        </Form.Item>
        <Form.Item {...formItemLayout} label="身份证号">
          {getFieldDecorator("identityNumber", {
            initialValue: account.identityNumber,
            rules: []
          })(<Input />)}
        </Form.Item>
        <Form.Item {...formItemLayout} label="年龄">
          {getFieldDecorator("age", {
            initialValue: account.age,
            rules: []
          })(<Input />)}
        </Form.Item>
        <Form.Item {...formItemLayout} label="公司">
          {getFieldDecorator("company", {
            initialValue: account.company,
            rules: []
          })(<Input />)}
        </Form.Item>
        <Form.Item {...formItemLayout} label="职位">
          {getFieldDecorator("position", {
            initialValue: account.position,
            rules: []
          })(<Input />)}
        </Form.Item>
        <Form.Item {...formItemLayout} label="个人介绍">
          {getFieldDecorator("personalProfile", {
            initialValue: account.personalProfile,
            rules: []
          })(<Input />)}
        </Form.Item>
        <Form.Item {...formItemLayout} label="性别">
          {getFieldDecorator("sex", {
            initialValue: account.sex,
            rules: []
          })(<Input />)}
        </Form.Item>
        <Form.Item {...formItemLayout} label="生日">
          {getFieldDecorator("birthday", {
            initialValue: account.birthday,
            rules: []
          })(<Input />)}
        </Form.Item>
        <Form.Item {...formItemLayout} label="学历">
          {getFieldDecorator("education", {
            initialValue: account.education,
            rules: []
          })(<Input />)}
        </Form.Item>
        <Form.Item {...formItemLayout} label="行业">
          {getFieldDecorator("profession", {
            initialValue: account.profession,
            rules: []
          })(<Input />)}
        </Form.Item>
        <Form.Item {...formItemLayout} label="">
          {getFieldDecorator("createAt", {
            initialValue: account.createAt,
            rules: []
          })(<Input />)}
        </Form.Item>
        <Form.Item {...formItemLayout} label="账号状态">
          {getFieldDecorator("status", {
            initialValue: account.status,
            rules: []
          })(<Input />)}
        </Form.Item>
        <Form.Item {...formItemLayout} label="">
          {getFieldDecorator("realPicture", {
            initialValue: account.realPicture,
            rules: []
          })(<Input />)}
        </Form.Item>
        <Form.Item {...tailFormItemLayout}>
          <Button type="primary" htmlType="submit">
            确定
          </Button>
        </Form.Item>
      </Form>
    );
  }
}

export default Form.create()(AccountForm);