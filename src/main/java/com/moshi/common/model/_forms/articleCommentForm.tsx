import { Button, Form, Input } from "antd";
import { FormComponentProps } from "antd/lib/form";
import React from "react";

class ArticleCommentForm extends React.Component<FormComponentProps & {
    articleComment: ArticleComment
}> {

  handleSubmit = (e: React.FormEvent<any>) => {
    e.preventDefault();
    this.props.form.validateFieldsAndScroll((err, values) => {
      if (!err) {
        
      }
    });
  };

  render() {
    const { articleComment } = this.props;
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
            initialValue: articleComment.id,
            rules: []
          })(<Input />)}
        </Form.Item>
        <Form.Item {...formItemLayout} label="">
          {getFieldDecorator("articleId", {
            initialValue: articleComment.articleId,
            rules: []
          })(<Input />)}
        </Form.Item>
        <Form.Item {...formItemLayout} label="">
          {getFieldDecorator("accountId", {
            initialValue: articleComment.accountId,
            rules: []
          })(<Input />)}
        </Form.Item>
        <Form.Item {...formItemLayout} label="">
          {getFieldDecorator("content", {
            initialValue: articleComment.content,
            rules: []
          })(<Input />)}
        </Form.Item>
        <Form.Item {...formItemLayout} label="">
          {getFieldDecorator("createAt", {
            initialValue: articleComment.createAt,
            rules: []
          })(<Input />)}
        </Form.Item>
        <Form.Item {...formItemLayout} label="">
          {getFieldDecorator("status", {
            initialValue: articleComment.status,
            rules: []
          })(<Input />)}
        </Form.Item>
        <Form.Item {...formItemLayout} label="">
          {getFieldDecorator("replyTo", {
            initialValue: articleComment.replyTo,
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

export default Form.create()(ArticleCommentForm);