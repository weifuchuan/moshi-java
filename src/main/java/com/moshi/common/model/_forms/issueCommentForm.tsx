import { Button, Form, Input } from "antd";
import { FormComponentProps } from "antd/lib/form";
import React from "react";

class IssueCommentForm extends React.Component<FormComponentProps & {
    issueComment: IssueComment
}> {

  handleSubmit = (e: React.FormEvent<any>) => {
    e.preventDefault();
    this.props.form.validateFieldsAndScroll((err, values) => {
      if (!err) {
        
      }
    });
  };

  render() {
    const { issueComment } = this.props;
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
        <Form.Item {...formItemLayout} label="">
          {getFieldDecorator("id", {
            initialValue: issueComment.id,
            rules: []
          })(<Input />)}
        </Form.Item>
        <Form.Item {...formItemLayout} label="">
          {getFieldDecorator("issueId", {
            initialValue: issueComment.issueId,
            rules: []
          })(<Input />)}
        </Form.Item>
        <Form.Item {...formItemLayout} label="">
          {getFieldDecorator("accountId", {
            initialValue: issueComment.accountId,
            rules: []
          })(<Input />)}
        </Form.Item>
        <Form.Item {...formItemLayout} label="">
          {getFieldDecorator("createAt", {
            initialValue: issueComment.createAt,
            rules: []
          })(<Input />)}
        </Form.Item>
        <Form.Item {...formItemLayout} label="">
          {getFieldDecorator("content", {
            initialValue: issueComment.content,
            rules: []
          })(<Input />)}
        </Form.Item>
        <Form.Item {...formItemLayout} label="">
          {getFieldDecorator("status", {
            initialValue: issueComment.status,
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

export default Form.create()(IssueCommentForm);