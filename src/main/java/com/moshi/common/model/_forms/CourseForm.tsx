import { Button, Form, Input } from "antd";
import { FormComponentProps } from "antd/lib/form";
import React from "react";

class CourseForm extends React.Component<FormComponentProps & {
    course: Course
}> {

  handleSubmit = (e: React.FormEvent<any>) => {
    e.preventDefault();
    this.props.form.validateFieldsAndScroll((err, values) => {
      if (!err) {
        
      }
    });
  };

  render() {
    const { course } = this.props;
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
            initialValue: course.id,
            rules: []
          })(<Input />)}
        </Form.Item>
        <Form.Item {...formItemLayout} label="作者id">
          {getFieldDecorator("accountId", {
            initialValue: course.accountId,
            rules: []
          })(<Input />)}
        </Form.Item>
        <Form.Item {...formItemLayout} label="专栏名">
          {getFieldDecorator("name", {
            initialValue: course.name,
            rules: []
          })(<Input />)}
        </Form.Item>
        <Form.Item {...formItemLayout} label="专栏简介">
          {getFieldDecorator("introduce", {
            initialValue: course.introduce,
            rules: []
          })(<Input />)}
        </Form.Item>
        <Form.Item {...formItemLayout} label="">
          {getFieldDecorator("introduceImage", {
            initialValue: course.introduceImage,
            rules: []
          })(<Input />)}
        </Form.Item>
        <Form.Item {...formItemLayout} label="订阅须知">
          {getFieldDecorator("note", {
            initialValue: course.note,
            rules: []
          })(<Input />)}
        </Form.Item>
        <Form.Item {...formItemLayout} label="创建时间">
          {getFieldDecorator("createAt", {
            initialValue: course.createAt,
            rules: []
          })(<Input />)}
        </Form.Item>
        <Form.Item {...formItemLayout} label="发布时间">
          {getFieldDecorator("publishAt", {
            initialValue: course.publishAt,
            rules: []
          })(<Input />)}
        </Form.Item>
        <Form.Item {...formItemLayout} label="购买人数">
          {getFieldDecorator("buyerCount", {
            initialValue: course.buyerCount,
            rules: []
          })(<Input />)}
        </Form.Item>
        <Form.Item {...formItemLayout} label="课程类型（1：专栏；2：视频）">
          {getFieldDecorator("courseType", {
            initialValue: course.courseType,
            rules: []
          })(<Input />)}
        </Form.Item>
        <Form.Item {...formItemLayout} label="标准价格">
          {getFieldDecorator("price", {
            initialValue: course.price,
            rules: []
          })(<Input />)}
        </Form.Item>
        <Form.Item {...formItemLayout} label="优惠价格">
          {getFieldDecorator("discountedPrice", {
            initialValue: course.discountedPrice,
            rules: []
          })(<Input />)}
        </Form.Item>
        <Form.Item {...formItemLayout} label="优惠到某时间">
          {getFieldDecorator("offerTo", {
            initialValue: course.offerTo,
            rules: []
          })(<Input />)}
        </Form.Item>
        <Form.Item {...formItemLayout} label="">
          {getFieldDecorator("status", {
            initialValue: course.status,
            rules: []
          })(<Input />)}
        </Form.Item>
        <Form.Item {...formItemLayout} label="">
          {getFieldDecorator("lectureCount", {
            initialValue: course.lectureCount,
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

export default Form.create()(CourseForm);